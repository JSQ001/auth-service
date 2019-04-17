import config from 'config';
import httpFetch from 'share/httpFetch';

export default {
  //获取公告信息列表，enabled: true(查询启用的)，false(查询启用及禁用的)，默认false
  getAnnouncementList(enabled = false) {
    return httpFetch.get(`${config.mdataUrl}/api/carousels/all`);
  },

  //根据公告信息Oid删除公告信息
  deleteAnnouncement(carouselOid) {
    return httpFetch.delete(`${config.mdataUrl}/api/carousels/${carouselOid}`);
  },

  //新建公告信息
  newAnnouncement(params) {
    return httpFetch.post(`${config.mdataUrl}/api/carousels`, params);
  },

  //更新公告信息
  updateAnnouncement(params) {
    return httpFetch.put(`${config.mdataUrl}/api/carousels`, params);
  },

  //获取公告信息详情
  getAnnouncementDetail(Oid) {
    return httpFetch.get(`${config.mdataUrl}/api/carousels/${Oid}`);
  },
  //获取公告图片模板
  getAnnouncementTemp() {
    return httpFetch.get(`${config.mdataUrl}/api/carousel/template?type=TEMPLATE&page=0&size=1000`);
  },
  //获取分配公司列表
  getCompanyList(page, size, Oid) {
    return httpFetch.get(
      `${
        config.mdataUrl
      }/api/carousels/company/find/distribution?page=${page}&size=${size}&carouselOid=${Oid}`
    );
  },

  //分配公司，carouselOids 和 companyOids 都是数组格式
  handleCompanyDistribute(carouselOids, companyOids) {
    let url = `${config.mdataUrl}/api/carousels/relevance/company`;
    carouselOids.map((Oid, index) => {
      if (index === 0) {
        url += `?carouselOids=${Oid}`;
      } else {
        url += `&carouselOids=${Oid}`;
      }
    });
    companyOids.map(Oid => {
      url += `&companyOids=${Oid}`;
    });
    return httpFetch.put(url);
  },

  //上传图片
  handleImageUpload(formData) {
    return httpFetch.post(`${config.baseUrl}/api/upload/static/attachment`, formData);
  },
};