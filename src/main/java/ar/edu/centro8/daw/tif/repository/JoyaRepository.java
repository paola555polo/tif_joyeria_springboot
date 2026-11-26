package ar.edu.centro8.daw.tif.repository;

import ar.edu.centro8.daw.tif.model.Joya;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JoyaRepository extends JpaRepository<Joya, Long> {
    List<Joya> findByCategoriaIgnoreCase(String categoria);
    List<Joya> findByMaterialIgnoreCase(String material);
}
