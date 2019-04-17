package com.hand.hcf.app.mdata.company.web;

import com.baomidou.mybatisplus.plugins.Page;
import com.hand.hcf.app.mdata.base.util.OrgInformationUtil;
import com.hand.hcf.app.mdata.company.dto.CompanyDTO;
import com.hand.hcf.app.mdata.company.service.CompanyService;
import com.hand.hcf.app.mdata.system.constant.CacheConstants;
import com.hand.hcf.app.mdata.utils.HeaderUtil;
import com.hand.hcf.app.core.exception.BizException;
import com.hand.hcf.app.core.util.PaginationUtil;
import com.hand.hcf.app.core.util.RespCode;
import io.micrometer.core.annotation.Timed;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Created by fanfuqiang 2018/11/27
 */
@RestController
@RequestMapping("/api/refactor")
public class RefactorCompanyResource {

    private final Logger log = LoggerFactory.getLogger(RefactorCompanyResource.class);

    @Autowired
    Environment env;

    @Autowired
    private CompanyService companyService;

    @Autowired
    RedisTemplate redisTemplate;

    //租户下创建公司接口
    @RequestMapping(value = "/tenant/company/register", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CompanyDTO> createCompanyV2(@Valid @RequestBody CompanyDTO companyDTO) throws URISyntaxException {
        if (companyDTO.getCompanyOid() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("company", "companyId.oid.not.null", "A create companyId request cannot have Oid")).body(null);
        }
        CompanyDTO result = null;
        // 创建公司用相同key
        byte[] lockKey = (OrgInformationUtil.getCurrentUserOid() + CacheConstants.COMPANY_SUBMIT).getBytes(Charset.forName("utf8"));
        Boolean locked = false;
        try {
            //check
            //jiu.zhao redis
            /*locked = (Boolean) redisTemplate.execute(new RedisCallback() {
                @Override
                public Boolean doInRedis(RedisConnection connection) throws DataAccessException {
                    Boolean result = connection.setNX(lockKey, "TRUE".getBytes(Charset.forName("utf8")));
                    connection.expire(lockKey, 60 * 2);
                    return result;
                }
            });
            if (!locked) {
                try {
                    log.error("operation is locked , lockKey : {}", new String(lockKey, "UTF-8"));
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                throw new BizException(RespCode.SYS_REQUEST_SPEED_IS_TOO_FAST, "请求速度过快");
            }*/
            Long tenantId = OrgInformationUtil.getCurrentTenantId();
            companyDTO.setTenantId(tenantId);
            result = companyService.createCompanyNew(companyDTO);
        } finally {
            //clean
            /*if (locked) {
                redisTemplate.execute(new RedisCallback() {
                    @Override
                    public Object doInRedis(RedisConnection connection) throws DataAccessException {
                        connection.del(lockKey);
                        return null;
                    }
                });
            }*/
        }
        return ResponseEntity.created(new URI("/tenant/companyId/register" + result.getCompanyOid()))
                .headers(HeaderUtil.createEntityCreationAlert("company", result.getCompanyOid().toString()))
                .body(result);
    }

    //后台更新公司配置信息
    @RequestMapping(value = "/companies",
            method = RequestMethod.PUT,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    // @PreAuthorize("hasRole('" + AuthoritiesConstants.ADMIN + "') or hasRole('" + AuthoritiesConstants.COMPANY_ADMIN + "')")
    public ResponseEntity<CompanyDTO> updateCompany(@Valid @RequestBody CompanyDTO companyDTO) throws URISyntaxException {
        log.debug("REST request to update Company : {}", companyDTO);
        companyDTO.setTenantId(OrgInformationUtil.getCurrentTenantId());
        if (companyDTO.getCompanyOid() == null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("company", "companyId.oid.is.null", "A update companyId request cannot miss Oid")).body(null);
        }
        CompanyDTO result = companyService.updateCompany(companyDTO, OrgInformationUtil.getCurrentUserOid());
        return ResponseEntity.ok()
                .headers(HeaderUtil.createEntityUpdateAlert("company", companyDTO.getCompanyOid().toString()))
                .body(result);
    }


    /**
     * GET  /companies/:id -> get the "id" companyId.
     */
    @RequestMapping(value = "/companies/{companyOid}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<CompanyDTO> getCompany(@PathVariable UUID companyOid) {
        log.debug("REST request to get Company : {}", companyOid);
        CompanyDTO companyDTO = companyService.getByCompanyOid(companyOid);
        return Optional.ofNullable(companyDTO)
                .map(result -> new ResponseEntity<>(
                        result,
                        HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }


    /**
     * 根据传入用户Oid查询用户所在帐套下的公司列表
     */
    @RequestMapping(value = "/companies/user/setOfBooks",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<CompanyDTO>> getUserSetOfBooksCompany(@RequestParam(value = "userOid", required = false) UUID userOid,
                                                                     @RequestParam(value = "setOfBooksId", required = false) Long setOfBooksId,
                                                                     @RequestParam(value = "name", required = false) String name,
                                                                     @RequestParam(value = "enabled", required = false) Boolean enabled,
                                                                     Pageable pageable) throws URISyntaxException {
        log.debug("REST request to get Companies by userOId : {}", userOid);
        if (userOid == null) {
            userOid = OrgInformationUtil.getCurrentUserOid();
        }
        Page<CompanyDTO> page = companyService.findUserSetOfBooksCompanys(userOid, setOfBooksId, name, enabled, pageable);
        HttpHeaders httpHeaders = PaginationUtil.generatePaginationHttpHeaders(page, "/api/companies/user/setOfBooks");
        return new ResponseEntity(page.getRecords(), httpHeaders, HttpStatus.OK);
    }
}
