package com.wahwahnetworks.platform.data.entities;

import org.apache.commons.lang.StringUtils;

import javax.persistence.*;
import java.time.Instant;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by jhaygood on 2/21/16.
 */

@Entity
@Table(name = "oauth_tokens")
public class OAuthToken {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name = "oauth_grant_id")
    private OAuthGrant grant;

    @Column(name = "expires_at")
    private Instant expirationTime;

    @Column(name = "auth_code")
    private String authorizationCode;

    @Column(name = "scopes")
    private String oauthScopes;

    public String getAuthorizationCode()
    {
        return authorizationCode;
    }

    public void setAuthorizationCode(String authorizationCode)
    {
        this.authorizationCode = authorizationCode;
    }

    public Set<String> getOauthScopes()
    {
        String[] scopes = this.oauthScopes.split(",");
        Set<String> resultSet = new HashSet<>();

        resultSet.addAll(Arrays.asList(scopes));

        return resultSet;
    }

    public void setOauthScopes(Set<String> oauthScopes)
    {
        String scope = StringUtils.join(oauthScopes,",");
        this.oauthScopes = scope;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public OAuthGrant getGrant() {
        return grant;
    }

    public void setGrant(OAuthGrant grant) {
        this.grant = grant;
    }

    public Instant getExpirationTime() {
        return expirationTime;
    }

    public void setExpirationTime(Instant expirationTime) {
        this.expirationTime = expirationTime;
    }
}
