package arami.common;

import java.util.Enumeration;
import java.util.Iterator;
import java.util.Map;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.ui.ModelMap;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import egovframework.com.cmm.service.EgovProperties;

public class Util {

    public static ModelMap getParameterModelMap(HttpServletRequest request) {

        ModelMap parameterMap = new ModelMap();
        Enumeration<?> enums = request.getParameterNames();
        while (enums.hasMoreElements()) {
            String paramName = (String) enums.nextElement();
            String[] parameters = request.getParameterValues(paramName);

            // Parameter가 배열일 경우
            if (parameters.length > 1) {
                parameterMap.put(paramName, parameters);
                // Parameter가 배열이 아닌 경우
            } else {
                if(paramName.endsWith("_arr")){
                    parameterMap.put(paramName, parameters);
                }else{
                    parameterMap.put(paramName, parameters[0]);
                }
            }
        }

        return parameterMap;
    }

    public static String getNullToEmptyString(Object obj){

        String rtnVal = "";

        if(obj == null){
            return "";
        }

        return obj.toString();
    }

    /**
	 * [0] 디버깅 목적
	 * @param  request   (↓ 현재 시점의 모델 )
	 * @return Debugging (↑ Console Print )
	 */
	public static void PrintModel(ModelMap model) {

	    if(EgovProperties.getProperty("Globals.debug").equals("true")) {
	        System.out.println("-------------- START -----------------");
	        for (String mapkey : model.keySet()){
	            System.out.println("[PrintModel] " + mapkey + "\t: "+ model.get(mapkey) );
	        }
	        System.out.println("--------------- END -----------------");
	    }

	}

	/**
	 * [0] 디버깅 목적
	 * @param  request   (↓ X )
	 * @return Debugging (↑ <<box>> 현재 메소드명 )
	 */
	public static void SystemOutPrintOut(String text, Object data) {
	    if(EgovProperties.getProperty("Globals.debug").equals("true")) {
	        System.out.println("▩▩▩▩▩▩▩▩▩▩▩▩▩▩▩▩▩▩▩▩▩▩▩▩▩▩▩▩▩▩▩▩▩▩▩▩▩▩▩▩▩▩▩▩▩▩▩▩▩▩▩▩▩▩▩▩▩▩▩▩▩▩▩");
	        Util.getBox();
	        System.out.println("[system.out] " + text + " ={} " + data + "\n");
	        System.out.println("▩▩▩▩▩▩▩▩▩▩▩▩▩▩▩▩▩▩▩▩▩▩▩▩▩▩▩▩▩▩▩▩▩▩▩▩▩▩▩▩▩▩▩▩▩▩▩▩▩▩▩▩▩▩▩▩▩▩▩▩▩▩▩");
	    }
	}

	/**
	 * [0] 디버깅 목적
	 * @param  request   (↓ X )
	 * @return Debugging (↑ <<box>> 현재 메소드명 )
	 */
	public static void getLine() {
	    if(EgovProperties.getProperty("Globals.debug").equals("true")) {
	        System.out.println("==========================================================\n");
	    }
	}


	/**
	 * [0] 디버깅 목적
	 * @param  request   (↓ X )
	 * @return Debugging (↑ <<box>> 현재 메소드명 )
	 */
	public static void getBox() {
	    if(EgovProperties.getProperty("Globals.debug").equals("true")) {
	        System.out.println("---- <<< box >>> : "+ Thread.currentThread().getStackTrace()[2].getClassName() + " :: "+Thread.currentThread().getStackTrace()[2].getMethodName() + " ---");
	    }
	}

	/**
	 * [0] 디버깅 목적
	 * @param  request   (↓ X )
	 * @return Debugging (↑ try catch 문에 걸린 에러 클래스명 출력 )
	 */
	public static void getErrorClass() {
	    if(EgovProperties.getProperty("Globals.debug").equals("true")) {
	        System.out.println("[system.error] Connection Exception occurred :: " + Thread.currentThread().getStackTrace()[2].getMethodName() );
	    }
	}

	/**
	 * [0] 디버깅 목적
	 * @param  request   (↓ X )
	 * @return Debugging (↑ 현재 메소드명 )
	 */
	public static String getClassName() {
	    return Thread.currentThread().getStackTrace()[2].getClassName();
	}


	/**
	 * [0] 디버깅 목적
	 * @param  request   (↓ 현재 시점의 파일 request )
	 * @return Debugging (↑ Console Print )
	 */
	public static void PrintFiles( MultipartHttpServletRequest request ) {
	    if(EgovProperties.getProperty("Globals.debug").equals("true")) {
	        Enumeration<?> params = request.getParameterNames();
	        System.out.println("----------- START -----------------");

	        while (params.hasMoreElements()) {
	            String name = (String) params.nextElement();
	            System.out.println("[system.out] :: " + name + " : " + request.getParameter(name));
	        }
	        System.out.println("----------- END -----------------");
	    }
	}


	/**
	 * [0] 디버깅 목적
	 * @param  request   (↓ 화면에서 입력이 들어오는 값 )
	 * @return Debugging (↑ Console Print )
	 */
	public static void PrintRequest(HttpServletRequest request) {
	    if(EgovProperties.getProperty("Globals.debug").equals("true")) {
	        System.out.println("-------------- START -----------------");
	        Enumeration<?> params = request.getParameterNames();
	        while (params.hasMoreElements()) {
	            String name = (String) params.nextElement();
	            System.out.println("[PrintRequest] " + name + "\t: " + request.getParameter(name));
	        }
	        System.out.println("--------------- END -----------------");
	    }
	}

	/**
	 * [0] 디버깅 목적
	 * @param  request   (↓ SQL 실행 결과 EgovMap )
	 * @return Debugging (↑ Console Print )
	 */
	public static void PrintMap(Map map) {
	    if(EgovProperties.getProperty("Globals.debug").equals("true")) {
	        System.out.println("-------------- START -----------------");
	        Iterator<String> k = map.keySet().iterator();
	        while(k.hasNext()){
	            String key = k.next();
	            System.out.println( "[PrintMap] "+ key + "\t: " + map.get(key));
	        }
	        System.out.println("--------------- END -----------------");
	    }
	}

}