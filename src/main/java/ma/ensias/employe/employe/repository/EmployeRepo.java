package ma.ensias.employe.employe.repository;
import ma.ensias.employe.employe.entite.Employe;
import org.springframework.data.jpa.repository.JpaRepository;
public interface EmployeRepo extends JpaRepository < Employe, Integer > {}