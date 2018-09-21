package jp.co.willwave.aca.service;

import jp.co.willwave.aca.dao.MasterArrivalNotesDao;
import jp.co.willwave.aca.dto.api.FinishRouteDetailMessageDTO;
import jp.co.willwave.aca.model.entity.MasterArrivalNotesEntity;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional(rollbackFor = Exception.class)
public class MasterArrivalNotesServiceImpl implements MasterArrivalNotesService {
    private final Logger logger = Logger.getLogger(MasterArrivalNotesServiceImpl.class);
    private final MasterArrivalNotesDao masterArrivalNotesDao;
    private final DivisionService divisionService;

    public MasterArrivalNotesServiceImpl(MasterArrivalNotesDao masterArrivalNotesDao,
                                         DivisionService divisionService) {
        this.masterArrivalNotesDao = masterArrivalNotesDao;
        this.divisionService = divisionService;
    }

    public List<FinishRouteDetailMessageDTO> getFinishedMessages(Long devicesId) {
        logger.info("MasterArrivalNotesLogic.getFinishedMessages");
        List<FinishRouteDetailMessageDTO> finishRouteDetailMessageDTOList = new ArrayList<>();
        Long companyId = divisionService.getCompanyIdManagedDevice(devicesId);
        List<MasterArrivalNotesEntity> masterArrivalNotesEntities = masterArrivalNotesDao.findByCompanyId(companyId);
        if (!CollectionUtils.isEmpty(masterArrivalNotesEntities)) {
            masterArrivalNotesEntities.forEach(message -> {
                FinishRouteDetailMessageDTO messageDTO = new FinishRouteDetailMessageDTO(message.getId(), message
                        .getName());
                finishRouteDetailMessageDTOList.add(messageDTO);
            });
        }
        return finishRouteDetailMessageDTOList;

    }
}
