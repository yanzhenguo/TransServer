package cn.shu.dao;

import cn.shu.entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TaskDao extends JpaRepository<Task,Integer>{
}
