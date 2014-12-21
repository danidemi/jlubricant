package com.danidemi.jlubricant.embeddable.hsql;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.danidemi.jlubricant.embeddable.database.core.Account;
import com.danidemi.jlubricant.embeddable.database.core.BaseAccount;

public class SetUserContribution implements PostStartContribution {

	private static final Logger log = LoggerFactory
			.getLogger(SetUserContribution.class);

	private Account desiredAccount;

	public SetUserContribution(BaseAccount desiredAccount2) {
		this.desiredAccount = desiredAccount2;
	}

	@Override
	public void apply(HsqlDatabaseDescriptor hsqlDatabase) throws SQLException {
		log.info("Creating new user {}", desiredAccount);
		try (Connection con = hsqlDatabase.getFastConnection()) {

			ResultSet rs;

			// first of all'let's check whether the specified username already
			// exists.
			// PreparedStatement prepareStatement =
			// con.prepareStatement("SELECT * FROM INFORMATION_SCHEMA.SYSTEM_USERS");
			// rs = prepareStatement.executeQuery();
			// while(rs.next()){
			// int columnCount = rs.getMetaData().getColumnCount();
			// for(int i=1; i<=columnCount; i++){
			// System.out.println(rs.getMetaData().getColumnName(i));
			// }
			// }

			PreparedStatement prepareStatement = con
					.prepareStatement("SELECT COUNT(*) FROM INFORMATION_SCHEMA.SYSTEM_USERS WHERE USER_NAME = ?");
			prepareStatement.setString(1, desiredAccount.getUsername());
			rs = prepareStatement.executeQuery();
			rs.next();
			boolean existingUser = rs.getLong(1) == 1L;
			rs.close();
			prepareStatement.close();

			PreparedStatement call;
			if (existingUser) {
				// throw new
				// IllegalArgumentException("Cannot change password to an existing user '"
				// + username + "'");
				log.info("User exists, altering it to use the new password.");
				call = con.prepareStatement("ALTER USER \""
						+ desiredAccount.getUsername() + "\" SET PASSWORD '"
						+ desiredAccount.getPassword() + "'");
				call.execute();
			} else {
				log.info("User does not exists, granting it ADMIN privileges.");
				call = con.prepareStatement("CREATE USER \""
						+ desiredAccount.getUsername() + "\" PASSWORD '"
						+ desiredAccount.getPassword() + "' ADMIN");
				call.execute();
			}

			call = con
					.prepareCall("SELECT * FROM INFORMATION_SCHEMA.SYSTEM_USERS");
			rs = call.executeQuery();
			while (rs.next()) {
				log.info("User " + rs.getObject(1) + " " + rs.getObject(2));
			}

			hsqlDatabase.setAccount(desiredAccount);

		} catch (SQLException e) {
			throw new RuntimeException(e);
		}

		try (Connection con = hsqlDatabase.getFastConnection()) {
			CallableStatement call = con
					.prepareCall("SELECT * FROM INFORMATION_SCHEMA.SYSTEM_USERS");
			ResultSet rs = call.executeQuery();
			while (rs.next()) {
				log.info("User " + rs.getObject(1) + " " + rs.getObject(2));
			}
		} catch (SQLException e) {
			new RuntimeException(e);
		}

	}

}
