package com.hand.hcf.app.mdata.contact.web;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.hand.hcf.app.core.util.PageUtil;
import com.hand.hcf.app.mdata.base.util.OrgInformationUtil;
import com.hand.hcf.app.mdata.contact.domain.ContactBankAccount;
import com.hand.hcf.app.mdata.contact.dto.ContactAccountDTO;
import com.hand.hcf.app.mdata.contact.dto.ContactBankAccountDTO;
import com.hand.hcf.app.mdata.contact.dto.UserDTO;
import com.hand.hcf.app.mdata.contact.service.ContactBankAccountService;
import com.hand.hcf.app.mdata.contact.service.ContactService;
import com.hand.hcf.app.mdata.contact.utils.UserInfoEncryptUtil;
import com.hand.hcf.app.mdata.externalApi.HcfOrganizationInterface;
import com.hand.hcf.app.mdata.utils.HeaderUtil;
import com.hand.hcf.app.core.domain.ExportConfig;
import com.hand.hcf.app.core.handler.ExcelExportHandler;
import com.hand.hcf.app.core.service.ExcelExportService;
import com.hand.hcf.app.core.util.LoginInformationUtil;
import io.micrometer.core.annotation.Timed;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * REST controller for managing ContactBankAccount.
 */
@RestController
@RequestMapping("/api")
public class ContactBankAccountResource {

    @Autowired
    private ContactBankAccountService contactBankAccountService;

    @Autowired
    private ExcelExportService excelExportService;

    @Autowired
    private HcfOrganizationInterface hcfOrganizationInterface;

    @Autowired
    private ContactService contactService;

    // create contactBankAccount
    @RequestMapping(value = "/contact/bank/account", method = RequestMethod.POST)
    @Timed
    public ResponseEntity<ContactBankAccountDTO> createContactBankAccount(@RequestBody ContactBankAccountDTO contactBankAccountDTO) {
        contactBankAccountDTO.setBankAccountNo(UserInfoEncryptUtil.encrypt(contactBankAccountDTO.getBankAccountNo()));
        return ResponseEntity.ok(
            contactBankAccountService.ContactBankAccountToContactBankAccountDTO(
                contactBankAccountService.createContactBankAccount(contactBankAccountDTO, OrgInformationUtil.getCurrentUserId())));
    }

    // update contactBankAccount
    @RequestMapping(value = "/contact/bank/account", method = RequestMethod.PUT)
    @Timed
    public ResponseEntity<ContactBankAccountDTO> updateContactBankAccount(@RequestBody ContactBankAccountDTO contactBankAccountDTO) {
        contactBankAccountDTO.setBankAccountNo(UserInfoEncryptUtil.encrypt(contactBankAccountDTO.getBankAccountNo()));
        return ResponseEntity.ok(
            contactBankAccountService.ContactBankAccountToContactBankAccountDTO(
                contactBankAccountService.updateContactBankAccount(contactBankAccountDTO, OrgInformationUtil.getCurrentUserId(),false)));
    }

    // get contactBankAccounts
    @RequestMapping(value = "/contact/bank/accounts", method = RequestMethod.GET)
    @Timed
    public ResponseEntity<List<ContactBankAccountDTO>> getAllContactBankAccount(Pageable pageable) {
        Page page = PageUtil.getPage(pageable);
        List<ContactBankAccountDTO> lists = contactBankAccountService.getALlContactBankAccount(page);
        return new ResponseEntity<>(lists, PageUtil.getTotalHeader(page), HttpStatus.OK);
    }

    // get contactBankAccounts by userOid
    @Timed
    @RequestMapping(value = "/contact/bank/account/enable", method = RequestMethod.GET)
    public ResponseEntity<List<ContactBankAccountDTO>> getEnableContactBankAccountByUserOid(@RequestParam(required = false) UUID userOid, Pageable pageable) throws URISyntaxException {
        //前端没有做判断,申请人为空的时候可能传空值过来,此时默认使用当前用户
        if (userOid == null || "".equals(userOid.toString())) {
            userOid = OrgInformationUtil.getCurrentUserOid();
        }
        Page page= PageUtil.getPage(pageable);
        List<ContactBankAccountDTO> lists = contactBankAccountService.getEnableContactBankAccountByUserOid(userOid, page);
        return new ResponseEntity<>(lists, PageUtil.getTotalHeader(page), HttpStatus.OK);
    }

    @Timed
    @RequestMapping(value = "/contact/bank/account/disable", method = RequestMethod.GET)
    public ResponseEntity<List<ContactBankAccountDTO>> getDisableContactBankAccountByUserOid(@RequestParam UUID userOid, Pageable pageable) throws URISyntaxException {
        Page page= PageUtil.getPage(pageable);
        List<ContactBankAccountDTO> lists = contactBankAccountService.getDisableContactBankAccountByUserOid(userOid, page);

        return new ResponseEntity<>(lists, PageUtil.getTotalHeader(page), HttpStatus.OK);
    }

    @Timed
    @RequestMapping(value = "/contact/bank/account", method = RequestMethod.GET)
    public ResponseEntity<List<ContactBankAccountDTO>> getContactBankAccountByUserOid(@RequestParam UUID userOid,
                                                                                      @RequestParam(value = "enable", required = false) Boolean enable,
                                                                                      Pageable pageable) throws URISyntaxException {
        Page page= PageUtil.getPage(pageable);
        List<ContactBankAccountDTO> lists = contactBankAccountService.getContactBankAccountByUserOid(userOid, enable, page);
        return new ResponseEntity<>(lists, PageUtil.getTotalHeader(page), HttpStatus.OK);
    }

    /**
     * @apiDescription 根据用户id查询他的银行卡信息
     * @api {GET} /api/contact/bank/account/user/id 用户id查询银行卡信息
     * @apiGroup ArtemisService
     * @apiParam {Long} userID 用户名称
     * @apiParam {boolean} enable 启用标志
     @apiSuccessExample {json} 成功返回值:
     [
     {
     "userOid": "6bdfc6b0-ed2c-442f-8743-22cd522c28c5",
     "contactBankAccountOid": "0ce013ae-876b-4ece-9a8c-10b3b8dfeb18",
     "bankAccountNo": "1234123412341234123",
     "bankAccountName": "清浅",
     "bankName": "",
     "branchName": "花旗银行美国分行",
     "accountLocation": "上海",
     "originalBankAccountNo": "1234***********4123",
     "isPrimary": true,
     "enable": true,
     "bankCode": "0000000000007",
     "tenantId": null
     }
     ]
     */
    @Timed
    @RequestMapping(value = "/contact/bank/account/user/id", method = RequestMethod.GET)
    public ResponseEntity<List<ContactBankAccountDTO>> getContactBankAccountByUserId(@RequestParam Long userID,
                                                                                     @RequestParam(value = "enable", required = false) Boolean enable,
                                                                                     Pageable pageable) throws URISyntaxException {

        UserDTO user = contactService.getUserDTOByUserId(userID);
        if(user==null){
            return ResponseEntity.ok(new ArrayList<>());
        }
        Page page= PageUtil.getPage(pageable);
        List<ContactBankAccountDTO> lists = contactBankAccountService.getContactBankAccountByUserOid(user.getUserOid(), enable, page);
        return new ResponseEntity<>(lists, PageUtil.getTotalHeader(page), HttpStatus.OK);
    }

    @Timed
    @RequestMapping(value = "/contact/bank/account/my", method = RequestMethod.GET)
    public ResponseEntity<List<ContactBankAccountDTO>> getMyContactBankAccountByUserOid(@RequestParam(value = "enable", required = false) Boolean enable, Pageable pageable) throws URISyntaxException {
        Page page= PageUtil.getPage(pageable);
        List<ContactBankAccountDTO> lists = contactBankAccountService.getContactBankAccountByUserOid(OrgInformationUtil.getCurrentUserOid(), enable, page);
        return new ResponseEntity<>(lists, PageUtil.getTotalHeader(page), HttpStatus.OK);
    }

    @Timed
    @RequestMapping(value = "/contact/bank/account/{bankAccountOid}", method = RequestMethod.GET)
    public ResponseEntity<ContactBankAccountDTO> getMyContactBankAccountByUserOid(@PathVariable UUID bankAccountOid) {
        return ResponseEntity.ok(contactBankAccountService.findOneByContactBankAccountOid(bankAccountOid));
    }

    //delete By userOid
    @Timed
    @RequestMapping(value = "/contact/bank/account", method = RequestMethod.DELETE)
    public ResponseEntity<Void> deleteContactBankAccountByUserOid(@RequestParam UUID userOid) {
        contactBankAccountService.deleteByUserOid(userOid);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("contactBankAccount", userOid.toString())).build();
    }

    @RequestMapping(value = "/contact/bank/account/{bankAccountOid}", method = RequestMethod.DELETE)
    public ResponseEntity<Void> deleteContactBankAccount(@PathVariable UUID bankAccountOid) {
        contactBankAccountService.deleteByBankAccountOid(bankAccountOid);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("contactBankAccount", bankAccountOid.toString())).build();
    }

    @RequestMapping(value = "/contact/bank/account/export/new")
    public void exportContactBankAccountNew(HttpServletRequest request,
                                            @RequestBody ExportConfig exportConfig,
                                            HttpServletResponse response,
                                            @RequestParam(required = false) String keyword,
                                            @RequestParam(required = false) String status,
                                            @RequestParam(required = false) List<UUID> departmentOid,
                                            @RequestParam(required = false) List<UUID> corporationOid,
                                            Pageable pageable) throws IOException {
        Page page = PageUtil.getPage(pageable);
        page.setSize(10000);
        List<UUID> userOids = contactService.listUserDTOByCondition(keyword == null ? null : keyword.trim(),OrgInformationUtil.getCurrentTenantId(),departmentOid,status,corporationOid,null,page)
                .stream().map(item -> item.getUserOid()).collect(Collectors.toList());
        List<ContactBankAccount> contactBankAccounts = contactBankAccountService.selectList(new EntityWrapper<ContactBankAccount>().in("user_oid",userOids));
        int total = contactBankAccounts.size();
        int threadNumber = total > 100000 ? 8 : 2;
        excelExportService.exportAndDownloadExcel(exportConfig, new ExcelExportHandler<ContactBankAccountDTO, ContactBankAccountDTO>() {
            @Override
            public int getTotal() {
                return total;
            }

            @Override
            public List<ContactBankAccountDTO> queryDataByPage(Page page) {
                return contactBankAccountService.exportContactBankAccountDTO(page,userOids);
            }

            @Override
            public ContactBankAccountDTO toDTO(ContactBankAccountDTO t) {
                return t;
            }

            @Override
            public Class<ContactBankAccountDTO> getEntityClass() {
                return ContactBankAccountDTO.class;
            }
        },threadNumber, request, response);
    }

    /**
     * 获取员工银行信息，（预付款，合同）
     * @param name
     * @param code
     * @param page
     * @param size
     * @return
     */
    @RequestMapping(value = "/contact/account/by/name/code", method = RequestMethod.GET)
    public ResponseEntity getReceivablesByNameAndCode(@RequestParam(value = "name",required = false) String name,
                                                      @RequestParam(value = "code",required = false) String code,
                                                      @RequestParam(value = "page",defaultValue = "0") int page,
                                                      @RequestParam(value = "size",defaultValue = "10") int size){
        Page queryPage = PageUtil.getPage(page, size);
        List<ContactAccountDTO> result = contactBankAccountService.getReceivablesByNameAndCode(name, code, queryPage);
        HttpHeaders httpHeaders = PageUtil.getTotalHeader(queryPage);
        return  new ResponseEntity(result,httpHeaders, HttpStatus.OK);
    }
}
