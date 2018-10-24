package com.example.service;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.mail.MessagingException;
import javax.servlet.http.HttpSession;

import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import com.example.UserFunction;
import com.example.api.SnsServiceFactory;
import com.example.config.SessionBindingListener;
import com.example.email.EmailHandler;
import com.example.email.TempKey;
import com.example.model.User;
import com.example.repository.ChatRoomRepository;
import com.example.repository.CityRepository;
import com.example.repository.FriendRepository;
import com.example.repository.GuRepository;
import com.example.repository.ImgRepository;
import com.example.repository.InfoRepository;
import com.example.repository.InterestRepository;
import com.example.repository.UserInterestRepository;
import com.example.repository.UserRepository;
import com.github.scribejava.core.model.OAuth2AccessToken;
import com.github.scribejava.core.model.OAuthRequest;
import com.github.scribejava.core.model.Response;
import com.github.scribejava.core.model.Verb;
import com.github.scribejava.core.oauth.OAuth20Service;

@Service
public class LoginService {

	@Autowired
	private JavaMailSender mailSender; // 메일 보내는 객체
	@Autowired
	private SnsServiceFactory service;
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private CityRepository cityRepository;
	@Autowired
	private GuRepository guRepository;
	@Autowired
	private InterestRepository interestRepository;
	@Autowired
	private UserFunction userFunction;
	@Autowired
	private ImgRepository imgRepository;
	@Autowired
	private UserInterestRepository userInterestRepository;
	@Autowired
	private InfoRepository infoRepository;
	@Autowired
	private ClubService clubService;
	@Autowired
	private ChatService chatService;
	@Autowired
	private FriendService friendService;
	@Autowired
	private FriendRepository friendRepository;
	@Autowired
	private ChatRoomRepository chatRoomRepository;
	
	// 이메일 체크
	public boolean emailcheck(String email) {
		if (userRepository.findByemail(email) == null) {
			return true;
		}
		return false;
	}

	// 회원가입(이메일인증까지)
	public void join(String id, String pw) throws UnsupportedEncodingException, MessagingException {
		User user = new User(id, userFunction.encrypt(pw), new TempKey().getKey(8, false));
		userRepository.save(user);

		EmailHandler mailHandler = new EmailHandler(mailSender);
		mailHandler.setSubject("[FRIEND] 가입 인증 메일입니다.");
		mailHandler.setText("<div style=\"position: relative;text-align:center;padding: 10px;\"><h1>FRIENDS에 가입해주셔서 감사합니다!</h1><h1>아래 버튼을 눌러 인증을 진행해주세요.</h1><br/><a href='http://192.168.0.200:8080/email/validate?email=" + user.getEmail() + "&authkey=" + user.getAuthkey() +"' style=\"text-decoration:none;position: relative;padding:15px 40px;color: white;background: rgb(51, 235, 235);border-radius: 10px;border: none;text-align: center;font-weight: bold;box-shadow: 0 0 15px -2px rgba(0,0,0,0.4);\">인증하기</a><br/></div>");
		mailHandler.setFrom("chtlstjd01@gmail.com", "운영자");
		mailHandler.setTo(user.getEmail());
		mailHandler.send();

	}

	// 권한키 확인
	public boolean checkKey(String authKey, String email) {
		if (userRepository.findByemail(email).getAuthkey().equals(authKey)) {
			userRepository.setAuth(email);
			return true;
		} else {
			return false;
		}
	}
	
	//상세정보 등록 여부 확인
   public Map<String, Object> isNewbie(String email) {
      Map<String, Object> map = new HashMap<>();
      int userid = userRepository.getUserid(email);
      //기타 마스터 자료 들
      map.put("dataSi", userFunction.ListToMap(cityRepository.getCityList(),"code"));
      map.put("dataGu", userFunction.ListToMapDupl(guRepository.getGuList(), "citycode", "gucode"));
      map.put("dataInterest", userFunction.ListToMap(interestRepository.getInterests(),"code"));
      map.put("contacts", chatService.ListChatRoom(userid));
      map.put("messages", chatService.ListChatMessage(userid));
      map.put("notifications", chatService.GetAlarmList(userid));
      map.put("myFriends", userFunction.ListToMap(friendRepository.getLoginFriendList(userid),"id"));
      map.put("popularGroups", clubService.topClub());
      map.put("popularFriends", friendService.popularUsers());
      map.put("token", userid);
      
      //상세정보가 등록되어 있으면
      if(userRepository.findByemail(email).getName() != null) {
    	      	  
         map.put("myInfo", getUserInfo(userid, true));
      }
      else {
         Map<String, Object> myInfo = new HashMap<>();
         myInfo.put("id", email);
         map.put("isNewbie", true);
         map.put("myInfo", myInfo);
      }
      return map;
   }

   // 로그인 결과
   public int login(String email, String pw, HttpSession session) {
      int result = 0;
      if (userRepository.findByemail(email) != null) {
         if (userRepository.findByemail(email).getPw().equals(userFunction.encrypt(pw))) {
            if (userRepository.findByemail(email).isAuth() == true) {
               session.setAttribute(email, new SessionBindingListener()); //세션을 HttpSessionBindingListener로 보냄
            } else result = 4; // 미인증 아이디
         } else result =  2; // 패스워드 불일치
      } else result =  1; // 계정 없음
      
      return result;
   }
	
	//추가 정보 항목들
	public Map<String, Object> extraInfo(String email){
		Map<String, Object> infoList = new HashMap<>();
		infoList.put("email", email);
		infoList.put("defaultInfo", null);
		infoList.put("defaultMsg", null);
		
		return infoList;
	}
	
	//패스워드 찾기
	public boolean password(String email) throws MessagingException, UnsupportedEncodingException {
		User user = userRepository.findByemail(email);
		if(user != null) {
			String pw = new TempKey().getKey(8, false);
			EmailHandler mailHandler = new EmailHandler(mailSender);

			userRepository.setPassword(email, userFunction.encrypt(pw));
			mailHandler.setSubject("[FRIEND] 임시 비밀번호입니다.");
			mailHandler.setText("<div style='position: relative;text-align:center;padding: 10px';><h2>로그인 후 마이페이지에서 변경해주세요.</h2><h1>" + pw + "</h1></div>");
			mailHandler.setFrom("chtlstjd01@gmail.com", "운영자");
			mailHandler.setTo(user.getEmail());
			mailHandler.send();
			return true;
		} 
		return false;
	}

	// authorization url
	public String getAuthorization(String name) {
		String url = null;
		if (name.equals("naver")) {
			url = service.naver().getAuthorizationUrl();
		} else if (name.equals("facebook")) {
			url = service.facebook().getAuthorizationUrl();
		} else if (name.equals("google")) {
			url = service.google().getAuthorizationUrl();
		}
		return url;
	}

	public Map<String, Object> getQuickSave(HttpSession session, String code, String state, String name)
			throws IOException, JSONException {
		// access token 발급
		OAuth2AccessToken accessToken = getAccessToken(session, code, state, name);

		// profile api를 호출해서 json형식으로 읽어옴
		String apiResult = getUserProfile(accessToken, name);

		// DB저장
		JSONObject obj = new JSONObject(apiResult);
		User user = new User();
		switch (name) {
		case "naver":
			user.setEmail(obj.getJSONObject("response").getString("id"));
			break;
		case "facebook":
			user.setEmail(obj.getString("id"));
			break;
		case "google":
			user.setEmail(obj.getString("id"));
		}
		if (userRepository.findByemail(user.getEmail()) == null) {
			userRepository.save(user);
		}
		int userid = userRepository.getUserid(user.getEmail());
		session.setAttribute(String.valueOf(userid), new SessionBindingListener());
		
		return isNewbie(user.getEmail());
	}

	// access token 발급
	public OAuth2AccessToken getAccessToken(HttpSession session, String code, String state, String name)
			throws IOException {
		// state : 난수130글자 일치 여부 확인
		String sessionState = (String) session.getAttribute("oauth_state");
		if (sessionState.equals(state)) {
			// AccessToken ȹ획득후 리턴
			if (name.equals("naver")) {
				return service.naver().getAccessToken(code);
			} else if (name.equals("facebook")) {
				return service.facebook().getAccessToken(code);
			} else {
				return service.google().getAccessToken(code);
			}
		}
		return null;
	}

	// api profile
	public String getUserProfile(OAuth2AccessToken accessToken, String name) throws IOException {
		OAuth20Service oauthService = null;
		String return_url = "";
		if (name.equals("naver")) {
			oauthService = service.naver();
			return_url = "https://openapi.naver.com/v1/nid/me";
		} else if (name.equals("facebook")) {
			oauthService = service.facebook();
			return_url = "https://graph.facebook.com/me?fields=id";
		} else {
			oauthService = service.google();
			return_url = "https://www.googleapis.com/plus/v1/people/me";
		}
		OAuthRequest request = new OAuthRequest(Verb.GET, return_url, oauthService);
		oauthService.signRequest(accessToken, request);
		Response response = request.send();
		return response.getBody();
	}
	
	public Map<String, Object> getUserInfo(int userid, boolean isMe){
	      
	      Map<String, Object> myInfo = new HashMap<>();
	      Map<String, Object> user = userRepository.getMyAllProfile(userid);
	      
	      //나이 계산
	      SimpleDateFormat sdf = new SimpleDateFormat("YYYY");
	      Date birth = (Date)user.get("birth");
	      int age = Integer.parseInt(sdf.format(new Date())) - Integer.parseInt(sdf.format(birth));
	      
	      List<String> imgList = imgRepository.getImgList(userid, 1);
	      for(int i = 0; i < imgList.size(); i++)
	         imgList.set(i, imgList.get(i));
	      
	      if(isMe) { //나일 때
	         myInfo.put("id", user.get("email"));
	      }else {
	         myInfo.put("id", user.get("userid"));
	      }
	      
	      myInfo.put("nickName", user.get("name"));
	      myInfo.put("si", user.get("city"));
	      myInfo.put("gu", user.get("gu"));
	      myInfo.put("birth", birth);
	      myInfo.put("age", age);
	      myInfo.put("gender", user.get("gender"));
	      myInfo.put("intro", user.get("intro"));
	      myInfo.put("msg", user.get("msg"));
	      myInfo.put("images", imgList);
	      myInfo.put("interests", userInterestRepository.getUserInterest(userid));
	      myInfo.put("areayn", user.get("areayn"));
	      myInfo.put("birthyn", user.get("birthyn"));
	      myInfo.put("genderyn", user.get("genderyn"));
	      myInfo.put("friendsyn", user.get("friendsyn"));
	      myInfo.put("groupsyn", user.get("groupsyn"));
	      
	      return myInfo;
	   }

	//1-3 :  사이트 소개
	public Map<String, Object> GetInfo () {
		return userFunction.ListToMap(infoRepository.GetInfo(), "id");
	}
}
