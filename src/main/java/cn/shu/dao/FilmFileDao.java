package cn.shu.dao;

import cn.shu.entity.FilmFile;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FilmFileDao extends JpaRepository<FilmFile,String> {
}
