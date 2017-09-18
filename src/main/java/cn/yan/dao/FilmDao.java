package cn.yan.dao;

import cn.yan.entity.Film;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FilmDao  extends JpaRepository<Film,String> {
}
