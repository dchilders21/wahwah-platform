package com.wahwahnetworks.platform.lib;

import com.wahwahnetworks.platform.models.git.GitFileListModel;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.eclipse.jgit.api.*;
import org.eclipse.jgit.api.errors.RefNotFoundException;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.errors.MissingObjectException;
import org.eclipse.jgit.lib.*;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.transport.*;
import org.eclipse.jgit.treewalk.CanonicalTreeParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import javax.servlet.ServletContext;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

/**
 * Created by Justin on 5/19/2014.
 */

@Component
public class GitManager {

    private static final Logger log = Logger.getLogger(GitManager.class);

    @Autowired
    private ServletContext servletContext;

    @Autowired
    private ApplicationContext applicationContext;

    private void configureSSH() throws IOException {

        // Extract Private Keys From Application
        File workDirectory = (File)servletContext.getAttribute("javax.servlet.context.tempdir");
        String keyDirectoryPath = workDirectory.getPath() + File.separator + "git_ssh_keys";
        File keyDirectory = new File(keyDirectoryPath);

        if(keyDirectory.exists()){
            try {
                log.info("SSH Private Keys Exist -- Deleting To Refresh From Application");
                FileUtils.deleteDirectory(keyDirectory);
            } catch (IOException ioException){
                log.error("Apparently an error occurred when deleting old SSH Private Keys...",ioException);
            }
        }

        File privateKeyDestinationFile = new File(keyDirectory.getPath() + File.separator + "wahwah_publisher");
        File publicKeyDestinationFile = new File(keyDirectory.getPath() + File.separator + "wahwah_publisher.pub");

        // Get & Copy Private Key Resource
        Resource privateKeyResource = applicationContext.getResource("classpath:git_keys/wahwah_publisher");
        File privateKeySourceFile = privateKeyResource.getFile();
        FileUtils.copyFile(privateKeySourceFile,privateKeyDestinationFile);

        // Get & Copy Public Key Resource
        Resource publicKeyResource = applicationContext.getResource("classpath:git_keys/wahwah_publisher.pub");
        File publicKeySourceFile = publicKeyResource.getFile();
        FileUtils.copyFile(publicKeySourceFile,publicKeyDestinationFile);

        WahwahGitJschConfigSessionFactory sessionFactory = new WahwahGitJschConfigSessionFactory(privateKeyDestinationFile.getPath());
        SshSessionFactory.setInstance(sessionFactory);
    }

    private File getGitDirectory(){

        log.info("Getting path to Git Repository");

        File workDirectory = (File)servletContext.getAttribute("javax.servlet.context.tempdir");
        String gitDirectoryPath = workDirectory.getPath() + File.separator + "git_ssh_wahwahbar";
        File gitDirectory = new File(gitDirectoryPath);

        log.info("Git Repository Path: " + gitDirectory.getAbsolutePath());

        return gitDirectory;
    }

    private Repository getRepository() throws Exception {

        log.info("Building JGit Repository Object");

        RepositoryBuilder repositoryBuilder = new RepositoryBuilder();
        Repository repository = repositoryBuilder.setWorkTree(getGitDirectory()).readEnvironment().findGitDir().build();
        return repository;
    }

    public byte[] getFileContents(String gitBranchName, String fileName, String commitId) throws Exception {

        File gitDirectory = getGitDirectory();

        if(!gitDirectory.exists()){
            cloneTree();
        }

        checkoutBranch(gitBranchName);
        checkoutCommit(gitBranchName,commitId);

        File fileToOpen = new File(gitDirectory.getAbsolutePath() + File.separator + fileName);

        Path filePathToOpen = fileToOpen.toPath();
        byte[] fileData = Files.readAllBytes(filePathToOpen);

        return fileData;
    }

    public GitFileListModel listNewFilesInBranch(String gitBranchName, String currentCommitId, String lastCommitId) throws Exception
    {
        if(lastCommitId == null){
            return listAllFilesInBranch(gitBranchName, currentCommitId);
        }

        GitFileListModel fileListModel = new GitFileListModel();

        log.info("listAllFilesInBranch");

        final Set<String> files = new TreeSet<>();

        File gitDirectory = getGitDirectory();

        if(!gitDirectory.exists()){
            cloneTree();
        }

        checkoutBranch(gitBranchName);

        ObjectId objectId;

        if(currentCommitId != null){
            checkoutCommit(gitBranchName,currentCommitId);
            objectId = ObjectId.fromString(currentCommitId);
        } else {
            objectId = getCurrentId();
        }

        ObjectId sinceObjectId = ObjectId.fromString(lastCommitId);

        Repository repository = getRepository();
        Git gitRepo = new Git(repository);

        ObjectReader reader = repository.newObjectReader();

        RevWalk oldRevisionWalker = new RevWalk(reader);

        RevCommit oldCommit;

        try {
            oldCommit = oldRevisionWalker.parseCommit(sinceObjectId);
        } catch (MissingObjectException exception){
            // This can happen if history is rewritten after the last publish. Just republish everything and next time we'll do just the diff.
            return listAllFilesInBranch(gitBranchName, currentCommitId);
        }

        RevWalk newRevisionWalker = new RevWalk(reader);
        RevCommit newCommit = newRevisionWalker.parseCommit(objectId);

        CanonicalTreeParser newTreeIterator = new CanonicalTreeParser();
        newTreeIterator.reset(reader,newCommit.getTree().getId());

        CanonicalTreeParser oldTreeIterator = new CanonicalTreeParser();
        oldTreeIterator.reset(reader,oldCommit.getTree().getId());

        List<DiffEntry> diffs = gitRepo.diff().setNewTree(newTreeIterator).setOldTree(oldTreeIterator).call();

        diffs.forEach(diff -> {

            String path = diff.getNewPath();

            if(diff.getChangeType() != DiffEntry.ChangeType.DELETE && !files.contains(path)){
                files.add(path);
            }
        });

        Set<String> filesToAdd = new HashSet<>();

        GitFileListModel allFiles = listAllFilesInBranch(gitBranchName, currentCommitId);

        files.forEach(file -> {
            if(allFiles.getFiles().contains(file)){
                filesToAdd.add(file);
            }
        });

        fileListModel.setCommitId(objectId.getName());
        fileListModel.setFiles(filesToAdd);

        newRevisionWalker.close();
        oldRevisionWalker.close();
        reader.close();

        return fileListModel;
    }

    public GitFileListModel listAllFilesInBranch(String gitBranchName, String commitId) throws Exception {

        GitFileListModel fileListModel = new GitFileListModel();

        log.info("listAllFilesInBranch");

        Set<String> files = new TreeSet<>();

        File gitDirectory = getGitDirectory();

        if(!gitDirectory.exists()){
            cloneTree();
        }

        checkoutBranch(gitBranchName);

        ObjectId objectId;

        if(commitId != null){
            checkoutCommit(gitBranchName,commitId);
            objectId = ObjectId.fromString(commitId);
        } else {
            objectId = getCurrentId();
        }

        Collection<File> fileCollection = FileUtils.listFiles(gitDirectory,null,true);

        for(File fileInCollection : fileCollection){

            String filePath = fileInCollection.getAbsolutePath().replace(gitDirectory.getAbsolutePath() + File.separator,"");

            if(!filePath.startsWith(".git")) {
                files.add(filePath);
            }
        }

        fileListModel.setFiles(files);
        fileListModel.setCommitId(objectId.getName());

        return fileListModel;
    }

    public Set<String> listBranches() throws Exception {

        log.info("listBranches");

        File gitDirectory = getGitDirectory();

        if(!gitDirectory.exists()){
            cloneTree();
        }

        Set<String> branches = new HashSet<>();

        Repository repository = getRepository();
        Git gitRepo = new Git(repository);

        ListBranchCommand listBranchCommand = gitRepo.branchList();
        listBranchCommand.setListMode(ListBranchCommand.ListMode.REMOTE);
        List<Ref> branchRefs = listBranchCommand.call();

        for(Ref branchRef : branchRefs){

            String refName = branchRef.getName();
            String branchName = refName.substring(refName.lastIndexOf('/')+1);

            branches.add(branchName);
        }

        return branches;
    }

    public String getCommitIdForBranch(String gitBranchName) throws Exception
    {
        log.info("getCommitIdForBranch");

        File gitDirectory = getGitDirectory();

        if(!gitDirectory.exists()){
            cloneTree();
        }

        checkoutBranch(gitBranchName);
        ObjectId objectId = getCurrentId();

        return objectId.getName();
    }

    public void cloneTree() throws Exception {

        log.info("Starting JGit Clone");

        try {
            File gitDirectory = getGitDirectory();

            configureSSH();

            CloneCommand cloneCommand = Git.cloneRepository();
            cloneCommand.setDirectory(gitDirectory);
            cloneCommand.setURI("git@bitbucket.org:wahwahnetworksllc/wahwah-bar.git");
            cloneCommand.setCloneAllBranches(true);

            log.info("Cloning..");

            cloneCommand.call();
        } catch (Exception ex){
            throw ex;
        }

        log.info("Finished JGit Clone");
    }

    public ObjectId update() throws Exception {

        log.info("Starting JGit Pull");

        configureSSH();

        File gitDirectory = getGitDirectory();

        if(!gitDirectory.exists()){
            cloneTree();
        }

        Repository repository = getRepository();
        Git gitRepo = new Git(repository);

        PullCommand pullCommand = gitRepo.pull();
        ObjectId objectId = pullCommand.call().getMergeResult().getNewHead();

        log.info("Finished JGit Pull");

        return objectId;
    }

    public ObjectId getCurrentId() throws Exception {

        log.info("Start Resolve Current Id");

        Repository repository = getRepository();
        ObjectId currentId = repository.resolve(repository.getFullBranch());

        log.info("Finish Resolve Current Id");

        return currentId;
    }

    public void resetTree() throws Exception {
        log.info("Hard Reset & Clean Of Local Tree");

        Repository repository = getRepository();
        Git gitRepo = new Git(repository);

        ResetCommand resetCommand = gitRepo.reset();
        resetCommand.setMode(ResetCommand.ResetType.HARD);
        resetCommand.call();

        CleanCommand cleanCommand = gitRepo.clean();
        cleanCommand.setCleanDirectories(true);
        cleanCommand.call();

        log.info("Finish Hard Reset & Clean Of Local Tree");
    }

    public void checkoutBranch(String branchName) throws Exception {

        log.info("Starting JGit Checkout Branch: " + branchName);

        resetTree();

        Repository repository = getRepository();

        if(repository.getBranch().equals(branchName)){
            return;
        }

        Git gitRepo = new Git(repository);

        try {
            CheckoutCommand checkoutCommand = gitRepo.checkout();
            checkoutCommand.setName(branchName);
            checkoutCommand.call();
        } catch (RefNotFoundException exception){

            configureSSH();

            log.info("Branch does not yet exist locally.. creating");

            CheckoutCommand checkoutCommand = gitRepo.checkout();
            checkoutCommand.setName(branchName);
            checkoutCommand.setStartPoint("origin/" + branchName);
            checkoutCommand.setUpstreamMode(CreateBranchCommand.SetupUpstreamMode.SET_UPSTREAM);
            checkoutCommand.setCreateBranch(true);
            checkoutCommand.call();
        }

        log.info("Finished JGit Checkout Branch: " + branchName);
    }

    public void checkoutCommit(String branchName, String commitId) throws Exception {

        log.info("Starting JGit Checkout Commit: " + commitId + " for branch: " + branchName);

        Repository repository = getRepository();

        if(repository.getBranch().equals(branchName) && repository.resolve(repository.getFullBranch()).getName().equals(commitId))
        {
            return;
        }

        Git gitRepo = new Git(repository);

        CheckoutCommand checkoutCommand = gitRepo.checkout();
        checkoutCommand.setName(branchName);
        checkoutCommand.setStartPoint(commitId);
        checkoutCommand.call();

        log.info("Finished JGit Checkout Commit: " + commitId + " for branch: " + branchName);
    }
}
