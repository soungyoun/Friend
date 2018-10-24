package com.example;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Base64.Encoder;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import com.example.model.Alarm;
import com.example.model.Img;
import com.example.repository.ImgRepository;

@Component
public class UserFunction {
	
	public final static String ImgPath = "http://localhost:8080"; 
	//public final static String gImgPath = "http://192.168.0.200:8080/"; 
	@Autowired
	private ImgRepository imgRepository;

    private final SimpMessagingTemplate template;
    public UserFunction(SimpMessagingTemplate template) {
        this.template = template;
    }
	
	//리스트 맵을 맵으로 바꿔주는 함수
	public Map<String, Object> ListToMap(List<Map<String,Object>> list, String keyname){
		Map<String, Object> map= new HashMap<>(); 
		String key = null;
		for(int j = 0 ; j < list.size(); j++) {
			key = list.get(j).get(keyname).toString();
			map.put(key, list.get(j)); 
		}
		return map;
	}

	//리스트맵의 key가 두개 일경우
	public Map<String, Map<String, Object>> ListToMapDupl(List<Map<String, Object>> list, String firstkey, String secondkey){
		Map<String, Map<String, Object>> resultMap= new HashMap<>(); 
		String  fkey,skey ;
		
		for(int j = 0 ; j < list.size(); j++) {
			fkey = list.get(j).get(firstkey).toString();
			skey = list.get(j).get(secondkey).toString();
			
			//추가할 자료
			Map<String, Object> subMap = new HashMap<>();
			subMap.put(skey, list.get(j));
			//기존 맵 가져오기
			Map<String, Object> oldMap = new HashMap<>();
			if ( resultMap.get(fkey) != null) {
				oldMap = resultMap.get(fkey);
			}
			
			//기존맵에 새로운 자료 추가
			oldMap.put(skey, list.get(j));
			
			//새로운맵 결과값에 넣기
			resultMap.put(fkey, oldMap);
			
		}
		return resultMap;
	}
	
	   //디폴트 메시지, 인트로 생성
	   public Map<String,Object> getDefaultMsg(){
	      List<String> defaultMsg = new ArrayList<>();
	      defaultMsg.add("안녕하세요. 반갑습니다.");
	      defaultMsg.add("행복한 하루 보내세요.");
	      defaultMsg.add("후후후");
	      
	      List<String> defaultIntro = new ArrayList<>();
	      defaultIntro.add("테스트입니다.");
	      defaultIntro.add("인트로입니다.");
	      
	      Map<String, Object> msg = new HashMap<>();
	      msg.put("defaultMsg", defaultMsg);
	      msg.put("defaultIntro", defaultIntro);
	      return msg;
	      
	   }

	 //DB에서 불러온 경로값으로 파일을 읽어들여 바이너리 형식의 문자열로 반환함
	   public List<String> fileload(List<String> direc) throws IOException {

	      List<String> imgList = new ArrayList<>();
	      
	      Encoder encodeder = Base64.getEncoder();
	      ByteArrayOutputStream bos = null;
	      FileInputStream fis = null;
	      File file = null;
	      byte[] buffer = new byte[1024];
	      int b;
	      byte[] encoded = null;
	      byte[] fileBytes = null;
	      String encodedString = null;
	      
	      for(String fileName : direc) {
	         
	         bos = new ByteArrayOutputStream();
	         
	         file = new File(fileName);
	         fis = new FileInputStream(file);
	         
	         while((b = fis.read(buffer)) != -1)
	            bos.write(buffer, 0, b);
	         
	         fileBytes = bos.toByteArray();
	         
	         encoded = encodeder.encode(fileBytes);
	         encodedString = new String(encoded);
	         
	         imgList.add(encodedString);
	         bos.flush();
	      }
	      fis.close();
	      bos.close();
	      
	      return imgList;
	   }	   
	   
	   // 파일 삭제
	   public void deleteFile(List<Map<String, Object>> imgs) {
	      File file = null;
	      for(Map<String, Object> img: imgs) {
	         file = new File((String) img.get("imgpath"));
	         file.delete();
	      }
	   }
	   
	   // 파일 저장을 위한 메소드
	   public String imgDecoder(String base64, int gubun) {
		   String fileUrlPath = null;
		   String fileName= null;
		   String fileSavePath = null;
		   //fileUrlPath = "http://192.168.0.200:8080/" ;
		   fileUrlPath = "" ;		   
		   fileName = String.valueOf(System.currentTimeMillis()) + ".jpg";
		   if (gubun == 1) {
			   fileUrlPath = fileUrlPath +"/fimg/"+ fileName;
			   fileSavePath="src/main/resources/static/fimg/" + fileName;  //이미지파일 경로  
		   }else if(gubun == 2){
			   fileUrlPath = fileUrlPath +"/gimg/"+ fileName;
			   fileSavePath ="src/main/resources/static/gimg/" + fileName;
		   }
	      String data = base64.split(",")[1];
	      try(FileOutputStream fos = new FileOutputStream(fileSavePath)){
	         byte[] imgByteArray = Base64.getDecoder().decode(data);
	         fos.write(imgByteArray);
	      } catch (FileNotFoundException e) {
	         e.printStackTrace();
	      } catch (IOException e) {
	         e.printStackTrace();
	      }
	      return fileUrlPath;
		}

		//이미지 추가
		public void imgUpdate(int gubun, int id, JSONArray imglist) {
			List<String> imgs = imgRepository.getImgList(id, gubun);
			try {
				List<String> newImg = new ArrayList<>();
				List<String> oldImg = new ArrayList<>();
			   if(imglist.length()==0) {
	               imgRepository.deleteByIdAndGubun(id, gubun);
	            }
			   else {
			
				for (int i = 0; i < imglist.length(); i++) {
					if (imglist.get(i) instanceof JSONObject) {
						newImg.add(imglist.getJSONObject(i).getString("src"));
					} else {
						oldImg.add(imglist.getString(i));
					}
				}
				if(imgs != null) {
					for (int i = 0; i < imgs.size(); i++) {
						for (int j = 0; j < oldImg.size(); j++) {
							if (imgs.get(i).equals(oldImg.get(j))) {
								break;
							}
							if (j == (oldImg.size() - 1) ) {
								// 내 db에 있는값이 클라이언트가 보내준 값에 없을때, delete
								imgRepository.deleteByIdAndGubunAndImgpath(id, gubun,imgs.get(i));
							}
						}
					}
				}
				for (int k = 0; k < newImg.size(); k++) {
					// insert ??
					Img img = new Img();
					img.setGubun(gubun);
					img.setFirstyn(false);
					img.setId(id);
					img.setImgpath(imgDecoder(newImg.get(k), gubun));
					img.setWritedate(new Date());
					imgRepository.save(img);

				}
				   }
			} catch (JSONException e1) {
				e1.printStackTrace();
			}
		}

	   //알람 소켓으로 전송
	   public void sendAlarmSocket(int userid, Map<String, Object>resultMap) {
		   template.convertAndSend("/topic/"+userid, resultMap);
	   }
	   
	 //Date정보를받으면 나이구하기
	   public int UserAge(Date date) {
	      Date d=new Date();
	      int age=d.getYear()-date.getYear()+1;
	      return age;
	   }
	   //나이정보를받으면 Date구하기
	   public Date UserDate(int age) {
	      Date nowDate=new Date();
	      nowDate.setYear(nowDate.getYear()-age+1);
	      return nowDate;
	   }
	   
	 //비밀번호 암호화
	   public String encrypt(String planText) {

	      StringBuffer sb = new StringBuffer();
	      try {
	         MessageDigest md = MessageDigest.getInstance("SHA-256");
	         md.update(planText.getBytes());
	         byte byteData[] = md.digest();

	         for (int i = 0; i < byteData.length; i++) {
	            sb.append(Integer.toString((byteData[i] & 0xff) + 0x100, 16).substring(1));
	         }
	      } catch (NoSuchAlgorithmException e) {
	         e.printStackTrace();
	      }
	      return sb.toString();
	   }
	 
	     //알람 true/false 전송
	      public void sendAlarmSocketBoolean(int userid,boolean bool) {
	    	  template.convertAndSend("/topic/"+userid,bool);
	      }
	      
}
