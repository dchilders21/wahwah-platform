### README server_tools ####

Most of these are deployment-related scripts.
These scripts are located where they should be on the server.

/home/tomcat/bin/copyerrorfiles.sh
-----------------------------------
Used by teamcity to copy error files (and avoid erasing it because of failed deployments)

Called in Build Steps with:
 ssh tomcat@104.131.120.197 /home/tomcat/bin/copyerrorfiles.sh

-----------------------------------