package com.hand.hcf.app.mdata.announcement.service;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.hand.hcf.app.base.implement.web.AttchmentControllerImpl;
import com.hand.hcf.app.common.co.AttachmentCO;
import com.hand.hcf.app.core.util.PageUtil;
import com.hand.hcf.app.mdata.announcement.domain.Carousel;
import com.hand.hcf.app.mdata.announcement.dto.CarouselDTO;
import com.hand.hcf.app.mdata.announcement.persistence.CarouselMapper;
import com.hand.hcf.app.mdata.company.domain.Company;
import com.hand.hcf.app.mdata.company.service.CompanyService;
import com.hand.hcf.app.mdata.system.constant.Constants;
import com.hand.hcf.app.mdata.utils.RespCode;
import com.hand.hcf.app.core.exception.BizException;
import com.hand.hcf.app.core.exception.core.ObjectNotFoundException;
import com.hand.hcf.app.core.service.BaseService;
import com.hand.hcf.app.core.util.LoginInformationUtil;
import com.hand.hcf.app.core.util.LoginInformationUtil;
import ma.glasnost.orika.MapperFacade;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/*
import com.hand.hcf.app.client.attachment.AttachmentCO;
*/

@Service
public class CarouselService extends BaseService<CarouselMapper, Carousel> {

    @Autowired
    private CarouselMapper carouselMapper;

    @Autowired
    private AttchmentControllerImpl attachmentClient;

    @Autowired
    private CompanyService companyService;

    @Autowired
    private MapperFacade mapperFacade;


    private void setAttachment(CarouselDTO dto){
        if(dto != null){
            if (StringUtils.hasText(dto.getAttachmentOid())){
                AttachmentCO attachment = attachmentClient.getByOid(dto.getAttachmentOid());
                dto.setAttachment(attachment);
            }
        }
    }

    /**
     * 通过公司Oid获取轮播图列表+详情
     * @param companyOid
     * @param enabled enabled == null 忽略enabled条件；enabled != null 根据enabled条件查询
     * @return
     */
    public List<CarouselDTO> listAllByCompanyOidAndEnabled(UUID companyOid, Boolean enabled) {
        List<Carousel> carousels = baseMapper.selectList(new EntityWrapper<Carousel>()
                .eq("company_oid", companyOid)
                .eq( enabled != null,"enabled", enabled)
                .orderBy("preferred_date",false));
        List<CarouselDTO> carouselDTOList = mapperFacade.mapAsList(carousels,CarouselDTO.class);
        carouselDTOList.stream().map(u->{
            setAttachment(u);
            return u;
        }).collect(Collectors.toList());
        return carouselDTOList;
    }

    /**
     * 通过公司Oid获取轮播图列表+详情
     * @param tenantID
     * @param enabled enabled == null 忽略enabled条件；enabled != null 根据enabled条件查询
     * @return
     */
    public List<CarouselDTO> listAllByByTenantIdAndCompanyOidIsNullAndEnabled(Long tenantID, Boolean enabled) {
        List<Carousel> carousels = baseMapper.selectList(new EntityWrapper<Carousel>()
                .eq("tenant_id", tenantID)
                .where("company_oid is null")
                .eq( enabled != null,"enabled", enabled)
                .orderBy("preferred_date",false));
        List<CarouselDTO> carouselDTOList = mapperFacade.mapAsList(carousels,CarouselDTO.class);
        carouselDTOList.stream().map(u->{
            setAttachment(u);
            return u;
        }).collect(Collectors.toList());
        return carouselDTOList;
    }

    /**
     * 根据carouselOid获取轮播图详情
     * @param carouselOid
     * @return
     */
    public CarouselDTO getCarouselByCarouselOid(UUID carouselOid) {
        Carousel carousel = selectOne(new EntityWrapper<Carousel>()
                .eq("carousel_oid", carouselOid));
        CarouselDTO carouselDTO = mapperFacade.map(carousel,CarouselDTO.class);
        setAttachment(carouselDTO);
        return carouselDTO;
    }

    public Carousel createCarousel(Carousel carousel) {
        carousel.setCarouselOid(UUID.randomUUID());
        carousel.setTenantId(LoginInformationUtil.getCurrentTenantId());
        carousel.setPreferredDate(carousel.getPreferredDate() == null ? ZonedDateTime.now() : carousel.getPreferredDate());
        carouselMapper.insert(carousel);
        return carousel;
    }

    public CarouselDTO updateCarousel(CarouselDTO carouselDTO) {
        if (carouselDTO.getId() == null) {
            throw new BizException(RespCode.Carousel_6041003);
        }
        Carousel carousel = carouselMapper.selectById(carouselDTO.getId());
        if (carousel == null) {
            throw new BizException(RespCode.Carousel_6041001);
        }
        // 版本不一致
//        if (!carousel.getVersionNumber().equals(carouselDTO.getVersionNumber())){
//            throw new BizException(RespCode.SYS_VERSION_IS_ERROR);
//        }
        carousel.setAttachmentOid(carouselDTO.getAttachmentOid());
        carousel.setTitle(carouselDTO.getTitle());
        carousel.setContent(carouselDTO.getContent());
        carousel.setPreferredDate(carouselDTO.getPreferredDate());
        carousel.setOutLinkFlag(carouselDTO.getOutLinkFlag());
        carousel.setEnabled(carouselDTO.getEnabled());
        carouselMapper.updateById(carousel);
        //修改所有公司级公告信息
        List<Carousel> carousels = carouselMapper.selectList(new EntityWrapper<Carousel>()
                .eq("source", carousel.getId()));
        carousels.stream().forEach(item -> {
            item.setAttachmentOid(carousel.getAttachmentOid());
            item.setTitle(carousel.getTitle());
            item.setContent(carousel.getContent());
            item.setPreferredDate(carousel.getPreferredDate());
            item.setOutLinkFlag(carousel.getOutLinkFlag());
            item.setEnabled(carousel.getEnabled());
            carouselMapper.updateById(item);
        });
        CarouselDTO result =  mapperFacade.map(carousel,CarouselDTO.class);
        setAttachment(result);
        return result;
    }

    public void deleteCarousel(UUID carouselOid) {
        CarouselDTO carousel = getCarouselByCarouselOid(carouselOid);
        if (carousel == null) {
            throw new BizException(RespCode.Carousel_6041001);
        } else {
            // 判断是否存在附件
            if (StringUtils.hasText(carousel.getAttachmentOid())){
                String[] strings = carousel.getAttachmentOid().split(",");
                List<String> stringList = Arrays.asList(strings);
                //List<UUID> uuidList = stringList.stream().map(UUID::fromString).collect(Collectors.toList());
                //公告只有source字段为空才删除附件
                if(carousel.getSource() == null) {
                    attachmentClient.deleteByOids(stringList);
                }
            }
            //删除source是carousel.getId()的公告
            carouselMapper.delete(new EntityWrapper<Carousel>()
                    .eq("source",carousel.getId()));
            //刪除
            carouselMapper.deleteById(carousel.getId());
        }
    }

    public void deployTenantCarouselToCompanyLogic(List<UUID> carouselOids, List<UUID> companyOids) {
        if (CollectionUtils.isEmpty(carouselOids) || CollectionUtils.isEmpty(companyOids)) {
            throw new BizException(RespCode.Carousel_6041004);
        }
        for (UUID companyOid : companyOids) {
            for (UUID carouselOid : carouselOids) {
                CarouselDTO tenantCarousel = getCarouselByCarouselOid(carouselOid);
                Long source = tenantCarousel.getId();
                List<Carousel> carouselList = carouselMapper.selectList(new EntityWrapper<Carousel>()
                        .eq("source",source)
                        .eq("company_oid", companyOid));
                if (carouselList == null || carouselList.size() == 0) {
                    Carousel existTitleOne = selectOne(new EntityWrapper<Carousel>()
                            .eq("title",tenantCarousel.getTitle())
                            .eq("company_oid", companyOid));
                    Carousel carousel = new Carousel();
                    carousel.setCompanyOid(companyOid);
                    carousel.setTenantId(tenantCarousel.getTenantId());
                    carousel.setTitle(tenantCarousel.getTitle());
                    if(existTitleOne != null) {
                        carousel.setTitle(tenantCarousel.getTitle() + Constants.TENANT_ADDITION);
                    }
                    carousel.setContent(tenantCarousel.getContent());
                    carousel.setEnabled(tenantCarousel.getEnabled());
                    carousel.setCarouselOid(UUID.randomUUID());
                    carousel.setPreferredDate(tenantCarousel.getPreferredDate() == null ? ZonedDateTime.now() : tenantCarousel.getPreferredDate());
                    carousel.setAttachmentOid(tenantCarousel.getAttachmentOid());
                    carousel.setOutLinkFlag(tenantCarousel.getOutLinkFlag());
                    carousel.setSource(source);
                    carouselMapper.insert(carousel);
                }
            }
        }
    }

    public Page<Company> findCarouselCompanyDeploy(UUID carouselOid, Long currentTenantID, Pageable pageable) {
//        Carousel carousel = carouselRepository.findByCarouselOID(carouselOID);
//        if (carousel == null) {
//            throw new BizException(RespCode.Carousel_6041001);
//        }
//        List<CarouselDeploy> carouselDeploys = carouselDeployService.findAllTenantDeploy(carousel.getId(), null, null);
//        List<Long> companyIds = carouselDeploys.stream().map(CarouselDeploy::getCompanyId).collect(Collectors.toList());
//        Page<Company> companyPage = companyService.findByIdsIn(companyIds, pageable);
//        return companyPage;

        Page mybatisPage = PageUtil.getPage(pageable);
        CarouselDTO carousel = getCarouselByCarouselOid(carouselOid);
        if(carousel == null ){
            throw new ObjectNotFoundException(Carousel.class,carouselOid);
        }
        List<Carousel> carouselList = baseMapper.selectList(new EntityWrapper<Carousel>()
                .eq("tenant_id", currentTenantID)
                .eq("source",carousel.getId()));
        List<Company> companies = carouselList.stream().map(u -> {
            UUID companyOid = u.getCompanyOid();
            Company company = companyService.getCompanyByCompanyOid(companyOid);
            return company;
        }).sorted(Comparator.comparing(Company::getCompanyCode)).collect(Collectors.toList());
        mybatisPage.setRecords(companies);
        return mybatisPage;
    }












}
