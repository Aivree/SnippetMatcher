/**
 * 
 */
package com.rainbowland.spring.service;

import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.encrypt.Encryptors;
import org.springframework.security.crypto.encrypt.TextEncryptor;
import org.springframework.security.crypto.keygen.KeyGenerators;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.ConnectionData;
import org.springframework.social.connect.ConnectionFactory;
import org.springframework.social.connect.ConnectionFactoryLocator;
import org.springframework.social.connect.ConnectionKey;
import org.springframework.social.connect.ConnectionRepository;
import org.springframework.social.connect.DuplicateConnectionException;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import com.rainbowland.spring.dao.SocialUserDao;
import com.rainbowland.spring.model.SocialUser;

/**
 * @author lvqiang
 * 
 */
@Service
public class RainbowlandConnectionRepository implements ConnectionRepository {

	@Autowired
	SocialUserDao socialUserDao;

	@Autowired
	ConnectionFactoryLocator connectionFactoryLocator;

	@Value("${social.crypto.password}")
	private String encryptionPassword;

	private TextEncryptor textEncryptor;

	private String email;

	public RainbowlandConnectionRepository(String email) {
		this.email = email;
	}

	@PostConstruct
	public void initializeTextEncryptor() {
		textEncryptor = Encryptors.text(encryptionPassword, KeyGenerators
				.string().generateKey());
	}

	@Override
	public void addConnection(Connection<?> connection) {
		ConnectionData connectionData = connection.createData();
		List<String> emails = socialUserDao
				.findEmailsByProviderAndProviderUserId(
						connectionData.getProviderId(),
						connectionData.getProviderUserId());
		if (!emails.isEmpty()) {
			throw new DuplicateConnectionException(new ConnectionKey(
					connectionData.getProviderId(),
					connectionData.getProviderUserId()));
		}
		List<SocialUser> socialUsers = socialUserDao.findByEmailAndProvider(
				email, connectionData.getProviderId());
		if (socialUsers.isEmpty()) {
			throw new DuplicateConnectionException(new ConnectionKey(
					connectionData.getProviderId(),
					connectionData.getProviderUserId()));
		}

		SocialUser socialUser = new SocialUser();
		socialUser.setEmail(email);
		socialUser.setDisplayName(connectionData.getDisplayName());
		socialUser.setProfileUrl(connectionData.getProfileUrl());
		socialUser.setImageUrl(connectionData.getImageUrl());
		socialUser.setProvider(connectionData.getProviderId());
		socialUser.setProviderUserId(connectionData.getProviderUserId());
		socialUser.setRank(1);

		socialUser.setAccessToken(textEncryptor.encrypt(connectionData
				.getAccessToken()));
		socialUser.setAccessTokenSecret(textEncryptor.encrypt(connectionData
				.getSecret()));
		socialUser.setRefreshToken(textEncryptor.encrypt(connectionData
				.getRefreshToken()));

		socialUser.setExpireTime(connectionData.getExpireTime());
		try {
			socialUserDao.save(socialUser);
		} catch (Exception e) {
			throw new DuplicateConnectionException(new ConnectionKey(
					connectionData.getProviderId(),
					connectionData.getProviderUserId()));
		}
	}

	@Override
	public MultiValueMap<String, Connection<?>> findAllConnections() {
		MultiValueMap<String, Connection<?>> connections = new LinkedMultiValueMap<String, Connection<?>>();
		List<SocialUser> socialUsers = socialUserDao.findByEmail(email);
		for (SocialUser socialUser : socialUsers) {
			ConnectionData connectionData = createConnectionData(socialUser);
			Connection<?> connection = createConnection(connectionData);
			connections.add(socialUser.getProvider(), connection);
		}
		return connections;
	}
	@Override
	public List<Connection<?>> findConnections(String arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <A> List<Connection<A>> findConnections(Class<A> arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public MultiValueMap<String, Connection<?>> findConnectionsToUsers(
			MultiValueMap<String, String> arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <A> Connection<A> findPrimaryConnection(Class<A> arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Connection<?> getConnection(ConnectionKey arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <A> Connection<A> getConnection(Class<A> arg0, String arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <A> Connection<A> getPrimaryConnection(Class<A> arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void removeConnection(ConnectionKey arg0) {
		// TODO Auto-generated method stub
	}

	@Override
	public void removeConnections(String arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void updateConnection(Connection<?> arg0) {
		// TODO Auto-generated method stub
	}

	private Connection<?> createConnection(ConnectionData connectionData) {
		ConnectionFactory<?> connectionFactory = connectionFactoryLocator
				.getConnectionFactory(connectionData.getProviderId());
		return connectionFactory.createConnection(connectionData);

	}

	private ConnectionData createConnectionData(SocialUser socialUser) {
		return new ConnectionData(socialUser.getProvider(),
				socialUser.getProviderUserId(), socialUser.getDisplayName(),
				socialUser.getProfileUrl(), socialUser.getImageUrl(),
				textEncryptor.decrypt(socialUser.getAccessToken()),
				textEncryptor.decrypt(socialUser.getAccessTokenSecret()),
				textEncryptor.decrypt(socialUser.getRefreshToken()),
				covertExpireTime(socialUser.getExpireTime()));
	}

	private Long covertExpireTime(Long expireTime) {
		return expireTime != null ? expireTime : 0;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public TextEncryptor getTextEncryptor() {
		return textEncryptor;
	}

	public void setTextEncryptor(TextEncryptor textEncryptor) {
		this.textEncryptor = textEncryptor;
	}
}