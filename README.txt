Release

	To create a new release...

	# mvn release:clean release:prepare release:perform -DautoVersionSubmodules=true -Dgpg.passphrase=<gpg_pwd>
	
	In case something goes wrong...
	
	# ant release.rollback; ./git-revert.sh 
