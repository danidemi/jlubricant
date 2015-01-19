Release

	To create a new release...

	# new way
	mvn versions:set -DnewVersion=0.0.15
	mvn clean test
	mvn versions:commit
	mvn clean deploy -Dgpg.passphrase=<gpg_pwd>

	# old way based on old policies
	# mvn release:clean release:prepare release:perform -DautoVersionSubmodules=true -Dgpg.passphrase=<gpg_pwd>
	
	In case something goes wrong...
	
	# ant release.rollback; ./git-revert.sh 
