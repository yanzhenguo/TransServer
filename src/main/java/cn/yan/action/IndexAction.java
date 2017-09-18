package cn.yan.action;

import cn.yan.dao.FilmDao;
import cn.yan.dao.FilmFileDao;
import cn.yan.dao.TaskDao;
import cn.yan.dao.UserDao;
import cn.yan.entity.Film;
import cn.yan.entity.FilmFile;
import cn.yan.entity.User;
import cn.yan.util.UserUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import sun.misc.BASE64Encoder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpSession;
import java.io.*;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.util.Date;
import java.util.List;

@RestController
public class IndexAction {
    @Autowired
    TaskDao taskDao;
    @Autowired
    UserDao userDao;
    @Autowired
    FilmDao filmDao;
    @Autowired
    FilmFileDao filmFileDao;
    private static final Logger log = LoggerFactory.getLogger(IndexAction.class);
    @GetMapping(value = "/login")
    public String login(HttpServletRequest req){
        HttpSession session = req.getSession();
        String key = UserUtil.generatePass(10);
        session.setAttribute("key", key);
        log.debug("session id:"+session.getId());
        return key;
    }

    /**
     * 验证用户名和密码是否正确有效
     * @param name
     * @param pass
     * @param req
     * @return
     */
    @GetMapping(value = "/loginValidate")
    public String loginValidate(@RequestParam("name") String name,
                                @RequestParam("pass") String pass,
                                HttpServletRequest req){
        log.debug("received pass:"+pass);
        HttpSession session = req.getSession();
        String key= (String)session.getAttribute("key");
        if(key==null) return "wrong";
        User user = new User();
        user.setName(name);
        ExampleMatcher matcher = ExampleMatcher.matching();
        Example<User> ex = Example.of(user, matcher);
        List<User> users = userDao.findAll(ex);
        //若用户不存在，验证不通过
        if(users==null || users.size()==0) return "wrong";
        user = users.get(0);
        //若该用户已过期，验证不通过，并删除该用户
        if(user.getExpireDate().before(new Date())) {
            userDao.delete(user);
            return "expire";
        }
        //验证密码是否正确
        String realPass = user.getPasswd()+key;
        try {
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            BASE64Encoder base64Encoder = new BASE64Encoder();
            String md5Pass = base64Encoder.encode(md5.digest(realPass.getBytes("utf-8")));
            log.debug("real pass:"+md5Pass);
            if(md5Pass.equals(pass)) {
                session.setAttribute("isLogin","true");
                return "success";
            }
            else return "wrong";
        }catch (Exception e){
            e.printStackTrace();
        }
        return "wrong";
    }

    /**
     * 主站请求生成用户名密码接口
     * @param filmId 节目Id
     * @return 生成的用户信息
     */
	@GetMapping(value = "/newTask")
	public User newTask(@RequestParam("filmId") String filmId ){
	    User user = new User();
	    user.setFilmId(Integer.valueOf(filmId));
	    user.setName(UserUtil.generateUserName(filmId));
	    user.setPasswd(UserUtil.generatePass(16));
	    Date date = new Date();
	    user.setCreateDate(date);
	    user.setUpdateDate(date);
	    user.setExpireDate(new Date(date.getTime()+259200000L));
	    User result = userDao.save(user);
	    return result;
    }

    /**
     * 获取文件
     * @param filmId
     * @param fileName
     * @param offset
     * @param res
     */
    @GetMapping(value = "/getFile")
    public void getFile(@RequestParam("filmId") String filmId,
                        @RequestParam("fileName") String fileName,
                        @RequestParam("offset") Integer offset,
                        HttpServletRequest req,
                        HttpServletResponse res){
        HttpSession session = req.getSession();
        if(!session.getAttribute("isLogin").equals("true")) return;
        //返回文件
        Film film=new Film();
        film.setFilmId(filmId);
        ExampleMatcher matcher = ExampleMatcher.matching();
        Example<Film> ex = Example.of(film, matcher);
        List<Film> films = filmDao.findAll(ex);
        if(films==null || films.size()==0) return;
        File file = new File(films.get(0).getPath()+File.separator+fileName);
        res.setHeader("content-type", "application/octet-stream");
        res.setContentType("application/octet-stream");
        res.setHeader("Content-Disposition", "attachment;filename=" + file.getName());
        byte[] buff = new byte[1024];
        BufferedInputStream bis = null;
        OutputStream os = null;
        try {
            os = res.getOutputStream();
            bis = new BufferedInputStream(new FileInputStream(file));
            bis.skip(offset);
            int i = bis.read(buff);
            while (i != -1) {
                os.write(buff, 0, i);
                os.flush();
                i = bis.read(buff);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (bis != null) {
                try {
                    bis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return ;
    }

    /**
     * 上传文件
     * @param path
     * @param req
     */
    @PostMapping(value = "/uploadFile")
    public void uploadFile(@RequestParam("path")String path, HttpServletRequest req){
        String localPath = path;
        FileOutputStream fos = null;
        ServletInputStream fis = null;
        try {
            fis = req.getInputStream();
            fos = new FileOutputStream(new File(localPath));
            byte[] temp = new byte[1024];
            int i = fis.read(temp);
            while (i != -1){
                fos.write(temp,0,temp.length);
                fos.flush();
                i = fis.read(temp);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 返回节目相关文件
     * @param filmId
     * @return
     */
    @GetMapping(value = "/getFileList")
    public List<FilmFile> getFileList(@RequestParam("filmId") String filmId,
                              HttpServletRequest req){
        HttpSession session = req.getSession();
        String isLogin= (String)session.getAttribute("isLogin");
        if(isLogin==null || !isLogin.equals("true")) return null;
        Film film=new Film();
        film.setFilmId(filmId);
        ExampleMatcher matcher = ExampleMatcher.matching();
        Example<Film> ex = Example.of(film, matcher);
        List<Film> films = filmDao.findAll(ex);
        if(films==null || films.size()==0) return null;
        FilmFile filmFile=new FilmFile();
        filmFile.setUuid(films.get(0).getUuid());
        Example<FilmFile> ex2 = Example.of(filmFile, matcher);
        List<FilmFile> filmFiles = filmFileDao.findAll(ex2);
        return filmFiles;
    }
}
