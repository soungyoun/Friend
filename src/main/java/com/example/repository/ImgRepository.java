package com.example.repository;

import java.util.List;
import java.util.Map;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.UserFunction;
import com.example.model.Img;

public interface ImgRepository extends JpaRepository<Img, Integer>{
	//최재현
	   @Query(value="select CONCAT('" + UserFunction.ImgPath + "', imgpath) imgpath from img where gubun=:gubun and id=:id",nativeQuery=true)
	   public List<String> getImgpath(@Param("gubun")int gubun,@Param("id")int id);
	   
	   @Query(value="select firstyn from img where gubun=:gubun and id=:id",nativeQuery=true)
	   public List<Boolean> getImgyn(@Param("gubun")int gubun,@Param("id")int id);
	   
	   @Query(value="select CONCAT('" + UserFunction.ImgPath + "', imgpath) imgpath from img where gubun=:gubun and id=:id group by id",nativeQuery=true)
	   public String getImg(@Param("gubun")int gubun,@Param("id")int id);
	   
	   @Transactional
	   @Query(value="delete from img where gubun=:gubun and id=:id",nativeQuery=true)
	   public void deleteImg(@Param("gubun")int gubun,@Param("id")int id);
	   
	    @Transactional
	    void deleteByIdAndGubun(int id,int gubun);	
	
	    @Transactional
	    void deleteByIdAndGubunAndImgpath(int id,int gubun,String imgpath);	
	
		//권샘찬
		@Query(value = "SELECT CONCAT('" + UserFunction.ImgPath + "', imgpath) imgpath FROM img WHERE gubun = :gubun AND id = :id", nativeQuery = true)
		public List<String> getImgList(@Param("id") int id, @Param("gubun") int gubun);
		
	   @Query(value="DELETE FROM img WHERE id = :userid", nativeQuery=true)
	   public void deleteImgList(@Param("userid") int userid);

	   @Query(value="INSERT INTO img(gubun, id, imgpath, firstyn, writedate) VALUES(:gubun, :userid, :imgpath, :firstyn)", nativeQuery=true)
	   public void insertImgList(@Param("gubun") int gubun, @Param("userid") int userid, @Param("imgpath") String imgpath, @Param("firstyn")boolean firstyn );
	   
	   //성기훈
	   // img테이블에서 구분과 아이디에 해당하는 이미지클래스
	   List<Img> findByGubunAndId(int gubun, int id);
	   // 1-1-4 img테이블에서 구분과 아이디에 해당하는 이미지경로
//	   @Query(value="select imgpath from img where gubun = :gubun and id = :id", nativeQuery=true)
//	   public List<String> imgpathByGubunAndId(@Param("gubun") int gubun, @Param("id") int id);

	// 1-1-4 img테이블에서 구분과 아이디에 해당하는 이미지경로 리스트
	   @Query(value = "select CONCAT('" + UserFunction.ImgPath + "', imgpath) imgpath from img where gubun = :gubun and id = :id", nativeQuery = true)
	   public List<String> imgpathListByGubunAndId(@Param("gubun") int gubun, @Param("id") int id);
	   
	   // 1-1-4 img테이블에서 구분과 아이디에 해당하는 이미지경로
	   @Query(value = "select CONCAT('" + UserFunction.ImgPath + "', imgpath) imgpath from img where gubun = :gubun and id = :id", nativeQuery = true)
	   public String imgpathByGubunAndId(@Param("gubun") int gubun, @Param("id") int id);
	   
}
