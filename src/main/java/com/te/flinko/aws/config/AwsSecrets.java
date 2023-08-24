package com.te.flinko.aws.config;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AwsSecrets {

	private String username;
	private String password;
	private String Username;
	private String Password;
	private String database;
	private String host;
	private String engine;
	private String port;
	private String dbInstanceIdentifier;
}
