package cn.yan.dao;

import cn.yan.entity.FilmFile;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FilmFileDao extends JpaRepository<FilmFile,String> {
}
