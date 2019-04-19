package com.hand.hcf.app.mdata.contact.web;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.hand.hcf.app.mdata.base.util.OrgInformationUtil;
import com.hand.hcf.app.mdata.contact.domain.ContactCard;
import com.hand.hcf.app.mdata.contact.dto.ContactCardDTO;
import com.hand.hcf.app.mdata.contact.service.ContactCardService;
import com.hand.hcf.app.mdata.contact.service.ContactService;
import com.hand.hcf.app.mdata.contact.utils.UserInfoEncryptUtil;
import com.hand.hcf.app.mdata.utils.RespCode;
import com.hand.hcf.app.core.domain.ExportConfig;
import com.hand.hcf.app.core.exception.BizException;
import com.hand.hcf.app.core.handler.ExcelExportHandler;
import com.hand.hcf.app.core.service.ExcelExportService;
import com.hand.hcf.app.core.util.PageUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Created by yangqi on 2017/1/12.
 */
@RestController
@RequestMapping(value = "/api/contact/cards")
public class ContactCardResource {

    @Autowired
    private ContactCardService contactCardService;
    @Autowired
    private ExcelExportService excelExportService;
    @Autowired
    private ContactService contactService;

    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<List<ContactCardDTO>> getUserContactCards(@RequestParam UUID userOid,
                                                                    @RequestParam(value = "enable",required = false) Boolean enable) {
        return ResponseEntity.ok(contactCardService.getUserContactCards(userOid,enable,null).stream()
            .map(c -> contactCardService.contactCardToDTO(c,OrgInformationUtil.getCurrentLanguage()))
            .collect(Collectors.toList()));
    }

    @RequestMapping(value = "/default", method = RequestMethod.GET)
    public ResponseEntity<ContactCardDTO> getUserDefaultContactCard(@RequestParam UUID userOid) {
        return ResponseEntity.ok(contactCardService.getUserDefaultCard(userOid));
    }

    @RequestMapping(value = "/enable", method = RequestMethod.GET)
    public ResponseEntity<List<ContactCardDTO>> getUserEnableContactCards(@RequestParam UUID userOid) {
        return ResponseEntity.ok(contactCardService.getUserEnableContactCards(userOid).stream()
            .map(c -> contactCardService.contactCardToDTO(c, OrgInformationUtil.getCurrentLanguage()))
            .collect(Collectors.toList()));
    }

    @RequestMapping(value = "/disable", method = RequestMethod.GET)
    public ResponseEntity<List<ContactCardDTO>> getUserDisableContactCards(@RequestParam UUID userOid) {
        return ResponseEntity.ok(contactCardService.getUserDisableContactCards(userOid).stream()
            .map(c -> contactCardService.contactCardToDTO(c,OrgInformationUtil.getCurrentLanguage()))
            .collect(Collectors.toList()));
    }

    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<ContactCardDTO> createUserContactCard(@RequestBody ContactCardDTO contactCardDTO) {
        if (contactCardDTO.getUserOid() == null) {
            throw new BizException(RespCode.USER_OID_NOT_NULL);
        }
        if (contactCardDTO.getContactCardOid() != null) {
            throw new BizException(RespCode.CARD_OID_MUST_NULL);
        }
        contactCardDTO.setCardNo(UserInfoEncryptUtil.encrypt(contactCardDTO.getCardNo()));
        return ResponseEntity.ok(contactCardService.contactCardToDTO(contactCardService.upsertContactCard(contactCardDTO, OrgInformationUtil.getCurrentUserId(),false),OrgInformationUtil.getCurrentLanguage()));
    }

    @RequestMapping(method = RequestMethod.PUT)
    public ResponseEntity<ContactCardDTO> updateUserContactCard(@RequestBody ContactCardDTO contactCardDTO) {
        if (contactCardDTO.getContactCardOid() == null) {
            throw new BizException(RespCode.CARD_OID_NOT_NULL);
        }
        contactCardDTO.setCardNo(UserInfoEncryptUtil.encrypt(contactCardDTO.getCardNo()));
        return ResponseEntity.ok(contactCardService.contactCardToDTO(contactCardService.upsertContactCard(contactCardDTO, OrgInformationUtil.getCurrentUserId(),false),OrgInformationUtil.getCurrentLanguage()));
    }

    @RequestMapping(value = "/export/new")
    public void exportContactCardNew(HttpServletRequest request,
                                        @RequestBody ExportConfig exportConfig,
                                        HttpServletResponse response,
                                        @RequestParam(required = false) String keyword,
                                        @RequestParam(required = false) String status,
                                        @RequestParam(required = false) List<UUID> departmentOid,
                                        @RequestParam(required = false) List<UUID> corporationOid,
                                        Pageable pageable) throws IOException {
        Page page = PageUtil.getPage(pageable);
        page.setSize(10000);
        List<UUID> userOids = contactService.listUserDTOByCondition(keyword == null ? null : keyword.trim(),
                OrgInformationUtil.getCurrentTenantId(),
                departmentOid,
                status,
                corporationOid,null,
                null,
                null,
                null,
                page)
                .stream().map(item -> item.getUserOid()).collect(Collectors.toList());
        List<ContactCard> contactCards = contactCardService.selectList(new EntityWrapper<ContactCard>().in("user_oid",userOids));
        int total = contactCards.size();
        int threadNumber = total > 100000 ? 8 : 2;
        excelExportService.exportAndDownloadExcel(exportConfig, new ExcelExportHandler<ContactCardDTO, ContactCardDTO>() {
            @Override
            public int getTotal() {
                return total;
            }

            @Override
            public List<ContactCardDTO> queryDataByPage(Page page) {
                return contactCardService.exportContactCardDTO(page,userOids);
            }

            @Override
            public ContactCardDTO toDTO(ContactCardDTO t) {
                return t;
            }

            @Override
            public Class<ContactCardDTO> getEntityClass() {
                return ContactCardDTO.class;
            }
        },threadNumber, request, response);
    }

}
