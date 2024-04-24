package cz.cvut.fit.household.service.interfaces;

import cz.cvut.fit.household.datamodel.entity.household.Household;
import cz.cvut.fit.household.datamodel.entity.maintenance.Maintenance;
import cz.cvut.fit.household.datamodel.entity.maintenance.MaintenanceCreationDTO;
import cz.cvut.fit.household.datamodel.entity.maintenance.MaintenanceStateDTO;
import cz.cvut.fit.household.datamodel.entity.Membership;

import java.util.List;
import java.util.Optional;

public interface MaintenanceService {

    public Maintenance addMaintenance(MaintenanceCreationDTO updatedMaintenance, Household household, Membership assignee, Membership reporter);

    public void deleteMaintenance(Long maintenanceId);

    public Maintenance stopGeneratingMaintenance(Maintenance maintenance);

    public Maintenance changeState(MaintenanceStateDTO updatedMaintenanceStateDTO, Long maintenanceId);

    Optional<Maintenance> findMaintenanceById(Long id);

    List<Maintenance> getAll();

    public Maintenance updateMaintenance(Long maintenanceId, MaintenanceCreationDTO updatedMaintenance, Household houseHold, Membership reporter, Membership assignee);
}
