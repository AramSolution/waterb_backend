package console.util;

public class ShareGpki {
	
	public ShareGpki(){
		
	}
	public static NewGpkiUtil getGpkiUtil(String targetServerId)throws Exception{
		NewGpkiUtil g = new NewGpkiUtil();
		// 이용기관 서버CN
		String myServerId = "SVR4671125001";

		// GPKI_HOME 환경변수 사용 (Docker: /home/aram/vol/gpki, 로컬: src/main/resources/libs/gpki)
		String gpkiHome = System.getenv("GPKI_HOME") != null
				? System.getenv("GPKI_HOME")
				: "src/main/resources/libs/gpki";

		// 이용기관 서버인증서 경로
		g.setCertFilePath(gpkiHome);
		String envCertFilePathName = gpkiHome + "/SVR4671125001_env.cer";
		String envPrivateKeyFilePathName = gpkiHome + "/SVR4671125001_env.key";

		// 이용기관 서버인증서 비밀번호
		String envPrivateKeyPasswd = "gunsan5968!!";

		// 이용기관 서버전자서명 경로
		String sigCertFilePathName = gpkiHome + "/SVR4671125001_sig.cer";
		String sigPrivateKeyFilePathName = gpkiHome + "/SVR4671125001_sig.key";

		// 이용기관 서버전자서명 비밀번호
		String sigPrivateKeyPasswd = "gunsan5968!!";


		// 이용기관 GPKI API 라이선스파일 경로
		g.setGpkiLicPath(gpkiHome);
		g.setEnvCertFilePathName(envCertFilePathName);
		g.setEnvPrivateKeyFilePathName(envPrivateKeyFilePathName);
		g.setEnvPrivateKeyPasswd(envPrivateKeyPasswd);
		// LDAP 의 사용유무
		// 미사용일 경우 암호화할 타겟의 인증서를 파일로 저장해놓고 사용하여야함.
		g.setIsLDAP(true);
		g.setMyServerId(myServerId);
		g.setSigCertFilePathName(sigCertFilePathName);
		g.setSigPrivateKeyFilePathName(sigPrivateKeyFilePathName);
		g.setSigPrivateKeyPasswd(sigPrivateKeyPasswd);
		
		g.setTargetServerIdList(targetServerId);

		g.init();
		return g;
	}
}
