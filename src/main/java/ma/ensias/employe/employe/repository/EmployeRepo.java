package ma.ensias.employe.employe.repository;
import ma.ensias.employe.employe.entite.Employe;
import org.springframework.data.jpa.repository.JpaRepository;
public interface EmployeRepo extends JpaRepository < Employe, Integer > {}

@Autowired
private JdbcTemplate jdbcTemplate;

public List<Employe> findByNom(String nom) {
    String sql = "SELECT * FROM employe WHERE nom = '" + nom + "'"; // ⚠️ concaténation
    return jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(Employe.class));
}
