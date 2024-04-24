package cz.cvut.fit.household.repository.maintenance;

import cz.cvut.fit.household.datamodel.entity.maintenance.MaintenanceTask;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MaintenanceTaskRepository extends JpaRepository<MaintenanceTask, Long> {
}
