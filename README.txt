Release

	To create a new release...

	# new way
	mvn clean deploy -Dgpg.passphrase=<gpg_pwd>

	# old way based on old policies
	# mvn release:clean release:prepare release:perform -DautoVersionSubmodules=true -Dgpg.passphrase=<gpg_pwd>
	
	In case something goes wrong...
	
	# ant release.rollback; ./git-revert.sh 
