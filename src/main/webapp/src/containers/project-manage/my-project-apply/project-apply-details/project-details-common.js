import React, { Component } from 'react';
import config from 'config';
import { connect } from 'dva';
import { routerRedux } from 'dva/router';
import moment from 'moment';
import DocumentBasicInfo from 'widget/Template/document-basic-info';
import ApproveHistory from 'widget/Template/approve-history-work-flow';
import SlideFrame from 'widget/slide-frame';
import ListSelector from 'components/Widget/list-selector';
import { Card, Spin, Tabs, message, Button, Row, Col, Modal } from 'antd';
import './project-details.less';
import ContractDetail from 'containers/contract/contract-approve/contract-detail-common';
import PropTypes from 'prop-types';
import ContractInfo from './contract-info';
import ChildrenItem from './children-item';
import NewProjectApply from '../new-project-apply';
// import NewProjectDetails from './new-project-details';
import CNewProjectDetails from './cnew-project-detail';
import CustomerDetails from './customer-details';
import BottomBar from './bottom-bar';
import detailsService from './details-service';

const { TabPane } = Tabs;

class ProjectApplyDetails extends Component {
  constructor(props) {
    super(props);
    this.state = {
      tabKey: '1',
      headerInfo: {}, // 头信息
      detailsInfo: {}, // 详细信息
      allInfo: {}, // 头部全部信息(头信息+详细信息)
      editFrameView: false, // 编辑侧滑框可见
      newDetailView: false, // 新建详情侧滑框可见
      newContractView: false, // 添加合同侧滑框可见
      model: {}, // 编辑侧滑框初始数据-(用于编辑赋予初值)
      detailsParams: {}, // 详情初始数据-(用于编辑赋予初值)
      selectorItem: {
        title: '添加合同信息',
        url: `${config.contractUrl}/api/project/requisition/contract/query/unrelated/by/pro/req/id`,
        searchForm: [
          { type: 'input', label: '合同编号', id: 'contractNumber' },
          { type: 'input', label: '合同名称', id: 'contractName' },
        ],
        columns: [
          { title: '合同编号', dataIndex: 'contractNumber' },
          { title: '合同类型', dataIndex: 'contractTypeName' },
          { title: '合同名称', dataIndex: 'contractName' },
          { title: '币种', dataIndex: 'currency' },
          { title: '总金额', dataIndex: 'amount' },
          {
            title: '操作',
            dataIndex: 'id',
            align: 'center',
            render: (value, record) => (
              <a
                onClick={e => {
                  this.reviewContractDetails(e, value, record);
                }}
              >
                查看
              </a>
            ),
          },
        ],
        key: 'id',
      },
      selectedContractData: undefined, // 已选择的合同数据数组
      accessView: false, // 使用权限字段对应的模态框可见
      selectedAccessData: [], // 已选中的权限数组（部门/公司/人员）
      lineLabel: {
        1002: 'fullName',
        1003: 'name',
        1004: 'name',
      },
      arrLabel: {
        1002: 'userList',
        1003: 'departmentList',
        1004: 'companyList',
      },
      releaseIdsType: undefined, // 人员权限弹窗类型
      releaseIdsExtraParams: {}, // 人员权限弹窗参数
      contractExtraParams: {
        // setOfBooksId: props.company.setOfBooksId,
        id: props.match.params.id,
      }, // 合同listselector额外参数
      accessLabelKey: 'name',
      accessValueKey: 'id',
      contractShow: false, // 合同详情可见
      contractId: '', // 默认合同id
      customerShow: false,
      customerId: '',
      parentApplyShow: false, // 父项目申请模态可见
      parentApplyId: '',
    };
  }

  componentDidMount = () => {
    this.getHeaderInfo();
  };

  // 获取头信息
  getHeaderInfo = () => {
    const { match } = this.props;
    detailsService
      .getHeaderInfo(match.params.id)
      .then(({ data }) => {
        // 赋值headerInfo的同时对detailsInfo赋值（启用，禁用，权限，实际日期）
        const headerInfo = this.dealWidthHeaderInfo(data);
        const detailsInfo = this.dealWidthDetailsInfo(data);
        this.setState({
          headerInfo,
          detailsInfo,
          allInfo: { ...data },
        });
      })
      .catch(err => {
        message.error(err.response.data.message);
      });
  };

  // 处理headerInfo数据
  dealWidthHeaderInfo = data => {
    const headerInfo = {
      ...data.projectRequisition,
      createdDate: data.projectRequisition.createdDate,
      formName: '项目申请单',
      statusCode: data.projectRequisition.status,
      // attachments: data.projectRequisition.attachmentOid || [],
      businessCode: data.projectRequisition.projectReqNumber,
      createByName: data.projectRequisition.createdByName,
      employeeName: data.projectRequisition.employeeName,

      contractFlag: data.projectRequisition.contractFlag,

      infoList: [
        { label: this.$t('common.applicant'), value: data.projectRequisition.employeeName },
        { label: this.$t('acp.company'), value: data.projectRequisition.companyName },
        { label: '项目名称', value: data.projectRequisition.projectName || '-' },
        { label: '项目负责人', value: data.projectRequisition.pmName || '-' },
        { label: '项目地', value: data.projectRequisition.projectLocation || '-' },
        { label: '项目编号', value: data.projectRequisition.projectNumber || '-' },
        {
          label: '父项目申请单编号',
          value:
            (
              <a
                onClick={e => {
                  this.showParentApplyDetails(e, data.projectRequisition.parentProReqId);
                }}
              >
                {data.projectRequisition.parentProReqNumber}
              </a>
            ) || '-',
        },
        { label: '项目类型', value: data.projectRequisition.projectTypeName || '-' },
        { label: '项目预算', value: '-' },
        {
          label: '客户',
          value:
            (
              <a
                onClick={e => {
                  this.showCustomerDetails(e, data.projectRequisition.customerId);
                }}
              >
                {/* {data.projectRequisition.customerName} */}
                {data.projectRequisition.customerId}
              </a>
            ) || '-',
        },
        //修改
        //{ label: '申请金额', value: data.projectRequisition.amount || '-' },
        //{ label: '币种', value: data.projectRequisition.currencyCode || '-' },
        { label: '数据来源', value: data.projectRequisition.dataOriginName || '-' },
        { label: '来源系统', value: data.projectRequisition.dataOriginSystemName || '-' },
      ],
      remark: data.projectRequisition.projectDes,
      diyLabel: {
        label: this.$t('common.project.flag'),
        value: (data.projectRequisition.projectFlag ? '已立项' : '未立项') || '-',
      },
    };
    return headerInfo;
  };

  // 处理与detailsInfo有关数据
  dealWidthDetailsInfo = data => {
    let detailsInfo = null;
    if (!data.startUseDate) {
      const { lineLabel } = this.state;
      let { accessLabelKey, accessValueKey } = this.state;
      detailsInfo = {
        ...data,
        projectRequisition: {
          applyEmployee: data.projectRequisition.applyEmployee,
          closeUseDate: data.projectRequisition.closeUseDate,
          startUseDate: data.projectRequisition.startUseDate,
          projectStartDate: data.projectRequisition.projectStartDate,
          projectCloseDate: data.projectRequisition.projectCloseDate,
        },
      };
      accessLabelKey = data.projectRequisition.applyEmployee
        ? lineLabel[data.projectRequisition.applyEmployee]
        : 'name';
      accessValueKey = data.projectRequisition.applyEmployee
        ? String(data.projectRequisition.applyEmployee) === '1003'
          ? 'id'
          : 'id'
        : 'id';
      const typeObj = this.setListSelectorType(data.projectRequisition.applyEmployee);
      this.setState({
        accessLabelKey,
        accessValueKey,
        releaseIdsType: typeObj.releaseIdsType || '',
        releaseIdsExtraParams: typeObj.releaseIdsExtraParams || {},
      });
    } else detailsInfo = {};
    return detailsInfo;
  };

  // tab选项卡切换
  tabChange = tabKey => {
    this.setState({ tabKey });
  };

  // 添加合同侧滑可见
  handleNewContract = () => {
    this.setState({ newContractView: true });
  };

  // 关闭合同侧滑
  closeContractModel = () => {
    this.setState({ newContractView: false });
  };

  // 确认添加合同
  handleAddContract = ({ result }) => {
    const { match } = this.props;
    if (result.length < 1) return;
    const tempArr = result.map(item => {
      return { proReqId: match.params.id, contractHeaderId: item.id };
    });
    detailsService
      .saveContractList(tempArr)
      .then(res => {
        if (res) {
          this.setState({ newContractView: false }, () => {
            this.contractRef.search();
            message.success('添加合同成功');
          });
        }
      })
      .catch(err => {
        message.error(err.response.data.message);
      });
  };

  // 编辑侧滑框可见
  handleEdit = () => {
    const { headerInfo } = this.state;
    this.setState({
      editFrameView: true,
      model: { ...headerInfo },
    });
  };

  // 关闭编辑侧滑框
  closeSlideForm = flag => {
    this.setState(
      {
        editFrameView: false,
      },
      () => {
        // 重新调用接口获取头信息
        if (flag) this.getHeaderInfo();
      }
    );
  };

  // 新建项目申请单详情侧滑框可见
  handleNewDetails = () => {
    this.setState({ newDetailView: true });
  };

  // 关闭项目申请单详细信息侧滑框
  closeNewDetailsSlideForm = flag => {
    this.setState(
      {
        newDetailView: false,
        detailsParams: {},
      },
      () => {
        // 此处如果详情数据是从头数据中分离出来的则调下方函数可直接刷新数据
        if (flag) this.getHeaderInfo();
      }
    );
  };

  // 编辑详情侧滑可见
  handleEditDetails = e => {
    e.preventDefault();
    const { detailsInfo } = this.state;
    this.setState({ newDetailView: true, detailsParams: { ...detailsInfo } });
  };

  // 跳转至合同详情
  reviewContractDetails = (e, id) => {
    e.preventDefault();
    this.setState({
      newContractView: false,
      contractShow: true,
      contractId: id,
    });
  };

  // 撤回
  handleWithDraw = () => {
    const { headerInfo } = this.state;
    const params = {
      entities: [
        {
          entityOid: headerInfo.documentOid,
          entityType: headerInfo.documentType,
        },
      ],
    };
    detailsService
      .withDrawApplyInfo(params)
      .then(res => {
        if (res) {
          message.success('撤回成功');
          const { dispatch } = this.props;
          dispatch(
            routerRedux.replace({
              pathname: '/project-manage/my-project-apply/my-project-apply',
            })
          );
        }
      })
      .catch(err => {
        message.error(err.response.data.message);
      });
  };

  // 查看权限
  handleAccessView = e => {
    e.preventDefault();
    const { detailsInfo, arrLabel } = this.state;
    if (detailsInfo && detailsInfo.projectRequisition) {
      const nowReleaseIdsList = [];
      const { applyEmployee } = detailsInfo.projectRequisition;
      detailsInfo[arrLabel[applyEmployee]].forEach(item => {
        if (applyEmployee) {
          if (Number(applyEmployee) === 1002) {
            nowReleaseIdsList.push({ key: item.id, id: item.id, fullName: item.fullName });
          } else {
            nowReleaseIdsList.push({ key: item.id, name: item.name, id: item.id });
          }
        }
      });
      this.setState({
        selectedAccessData: nowReleaseIdsList,
        accessView: true,
      });
    }
  };

  // 设置权限listselector的type
  setListSelectorType = applyEmployee => {
    const typObj = {};
    const { company } = this.props;

    switch (applyEmployee) {
      case 1002:
      case '1002':
        //  按人员添加
        // typObj.releaseIdsType = 'contract_user';
        // typObj.releaseIdsExtraParams = { companyId: company.id };
        typObj.releaseIdsType = 'select_setOfBooksId_employee';
        typObj.releaseIdsExtraParams = {};
        break;
      case 1003:
      case '1003':
        //  按部门添加
        // typObj.releaseIdsType = 'deptCode';
        typObj.releaseIdsType = 'select_setOfBooksId_department';
        typObj.releaseIdsExtraParams = {};
        break;
      case 1004:
      case '1004':
        //  按公司添加
        typObj.releaseIdsType = 'company';
        typObj.releaseIdsExtraParams = { setOfBooksId: company.setOfBooksId };
        break;
      default:
        typObj.releaseIdsType = 'company';
        typObj.releaseIdsExtraParams = { setOfBooksId: company.setOfBooksId };
    }
    return typObj;
  };

  // 权限模态框关闭
  hideAccessModal = () => {
    this.setState({ accessView: false });
  };

  // 父项目申请单可见
  showParentApplyDetails = (e, id) => {
    e.preventDefault();
    this.setState({
      parentApplyId: id,
      parentApplyShow: true,
    });
  };

  // 客户信息可见
  showCustomerDetails = (e, id) => {
    e.preventDefault();
    this.setState({
      customerId: id,
      customerShow: true,
    });
  };

  // 渲染详情card的标题
  renderTitle = () => {
    const { onlyChange, readOnly } = this.props;
    const { headerInfo, detailsInfo } = this.state;
    let btnLabel = '';
    if (onlyChange) {
      btnLabel = '变更';
    } else if (
      detailsInfo.projectRequisition &&
      detailsInfo.projectRequisition.startUseDate &&
      Number(headerInfo.statusCode) !== 1004 &&
      Number(headerInfo.statusCode) !== 1002
    ) {
      btnLabel = this.$t('common.edit');
    } else if (readOnly) {
      return '详细信息';
    } else return '详细信息';

    if (btnLabel) {
      return (
        <span>
          详细信息{' '}
          <a style={{ marginLeft: '20px' }} onClick={e => this.handleEditDetails(e)}>
            {btnLabel}
          </a>
        </span>
      );
    }
  };

  // 渲染权限超链接
  renderAccessView = () => {
    let len = '';
    let label = '';
    const { detailsInfo, arrLabel } = this.state;
    if (detailsInfo.projectRequisition && JSON.stringify(detailsInfo.projectRequisition) !== '{}') {
      const { applyEmployee } = detailsInfo.projectRequisition;
      if (!applyEmployee || Number(applyEmployee) === 1001) {
        return <span />;
      } else {
        len = detailsInfo[arrLabel[applyEmployee]].length;
        label =
          Number(applyEmployee) === 1002
            ? '人员'
            : Number(applyEmployee) === 1003
              ? '部门'
              : '公司';
        return <span>{`已选择 ${len} ${label}`}</span>;
      }
    } else return <span />;
  };

  // 渲染详情页面新增按钮
  renderNewBtn = () => {
    const { readOnly, onlyChange } = this.props;
    const { detailsInfo, headerInfo } = this.state;
    if (onlyChange || readOnly) {
      return <span />;
    } else if (
      (!detailsInfo.projectRequisition || !detailsInfo.projectRequisition.startUseDate) &&
      Number(headerInfo.statusCode) === 1001
    ) {
      // 编辑中状态下且详情card无数据下展示新增btn
      return (
        <Button type="primary" onClick={this.handleNewDetails}>
          新建
        </Button>
      );
    } else return <div />;
  };

  render() {
    const {
      tabKey,
      headerInfo,
      editFrameView,
      newDetailView,
      newContractView,
      model,
      allInfo,
      detailsInfo,
      detailsParams,
      selectorItem,
      selectedContractData,
      accessView,
      selectedAccessData,
      releaseIdsType,
      releaseIdsExtraParams,
      contractExtraParams,
      contractShow,
      contractId,
      customerShow,
      customerId,
      parentApplyId,
      parentApplyShow,
      accessLabelKey,
      accessValueKey,
    } = this.state;

    const { readOnly, match } = this.props;

    let status = null;
    let btnLabel = '';
    let btnMethod = null;
    let isBtn = '';

    if (!readOnly) {
      if (
        Number(headerInfo.statusCode) === 1001 ||
        Number(headerInfo.statusCode) === 1005 ||
        Number(headerInfo.statusCode) === 1003
      ) {
        btnLabel = this.$t('common.edit');
        isBtn = true;
        btnMethod = this.handleEdit;
      } else if (Number(headerInfo.statusCode) === 1002) {
        // 审批中
        isBtn = true;
        btnLabel = this.$t('common.withdraw');
        btnMethod = this.handleWithDraw;
      }
    } else isBtn = false;

    if (isBtn) {
      status = (
        <h3 className="header-title" style={{ margin: '0' }}>
          <Button
            type="primary"
            style={{ marginBottom: '10px', float: 'right' }}
            onClick={btnMethod}
          >
            {btnLabel}
          </Button>
        </h3>
      );
    } else status = <div />;
    return (
      <div className="project-apply-details">
        <Spin spinning={false} wrapperClassName="info-box">
          <Card className="header-info">
            <DocumentBasicInfo params={headerInfo} isDiy>
              {status}
            </DocumentBasicInfo>
          </Card>
          <Card
            className="details-info"
            title={
              // detailsInfo有数据且非已审批状态下展示 编辑btn
              this.renderTitle()
            }
          >
            {this.renderNewBtn()}
            <Row style={{ margin: '15px 0' }}>
              <Col span={12}>
                <Row>
                  <Col span={8}>
                    <h3 className="details-info-h3">启用日期~禁用日期:</h3>
                  </Col>
                  <Col span={12}>
                    {detailsInfo.projectRequisition && detailsInfo.projectRequisition.startUseDate
                      ? moment(detailsInfo.projectRequisition.startUseDate).format('YYYY-MM-DD')
                      : '-'}{' '}
                    ~{' '}
                    {detailsInfo.projectRequisition && detailsInfo.projectRequisition.closeUseDate
                      ? moment(detailsInfo.projectRequisition.closeUseDate).format('YYYY-MM-DD')
                      : '-'}
                  </Col>
                </Row>
              </Col>
              <Col span={12}>
                <Row>
                  <Col span={8}>
                    <h3>项目实际实施起止日期:</h3>
                  </Col>
                  <Col span={12}>
                    {detailsInfo.projectRequisition &&
                    detailsInfo.projectRequisition.projectStartDate
                      ? moment(detailsInfo.projectRequisition.projectStartDate).format('YYYY-MM-DD')
                      : '-'}{' '}
                    ~{' '}
                    {detailsInfo.projectRequisition &&
                    detailsInfo.projectRequisition.projectCloseDate
                      ? moment(detailsInfo.projectRequisition.projectCloseDate).format('YYYY-MM-DD')
                      : '-'}
                  </Col>
                </Row>
              </Col>
            </Row>
            <Row>
              <Col span={4}>
                <h3>使用权限:</h3>
              </Col>
              <Col span={12}>
                {detailsInfo.projectRequisition &&
                Number(detailsInfo.projectRequisition.applyEmployee) === 1001 ? (
                  '全部人员'
                ) : (
                  <a
                    onClick={e => {
                      this.handleAccessView(e);
                    }}
                  >
                    {this.renderAccessView()}
                  </a>
                )}
              </Col>
            </Row>
          </Card>
        </Spin>
        <Card className="tabs-table">
          <Tabs defaultActiveKey={tabKey} onChange={this.tabChange}>
            <TabPane tab={this.$t('my.contract.info')} key="1">
              {/* 合同信息 */}
              <div style={{ margin: '10px 0' }}>
                {!readOnly ? (
                  headerInfo.contractFlag &&
                  (Number(headerInfo.statusCode) === 1001 ||
                    Number(headerInfo.statusCode) === 1003 ||
                    Number(headerInfo.statusCode) === 1005) ? (
                    <Button
                      type="primary"
                      onClick={this.handleNewContract}
                      style={{ margin: '10px 0' }}
                    >
                      添加合同
                    </Button>
                  ) : (
                    <span />
                  )
                ) : (
                  <span />
                )}
                <ContractInfo
                  headerInfo={headerInfo}
                  id={match.params.id}
                  // eslint-disable-next-line no-return-assign
                  ref={ref => (this.contractRef = ref && ref.wrappedInstance)}
                />
              </div>
            </TabPane>
            <TabPane tab="子项目申请单信息" key="2">
              <div style={{ margin: '10px 0' }}>
                <ChildrenItem headerInfo={headerInfo} id={match.params.id} />
              </div>
            </TabPane>
          </Tabs>
        </Card>
        {String(headerInfo.statusCode) === '1002' || String(headerInfo.statusCode) === '1004' ? (
          <ApproveHistory
            type="801011"
            oid={headerInfo.documentOid || ''}
            style={{ marginBottom: '50px' }}
          />
        ) : (
          // 审批中或已审批下展示审批历史
          <div />
        )}
        {/* 编辑侧滑 */}
        <SlideFrame
          title="编辑项目申请单基本信息"
          show={editFrameView}
          onClose={() => {
            this.closeSlideForm(false);
          }}
        >
          <NewProjectApply
            params={model}
            allInfo={allInfo}
            isNew={false}
            show={editFrameView}
            isReq={match.params.isReq}
            isView={match.params.isView}
            onClose={this.closeSlideForm}
          />
        </SlideFrame>
        {/* 新建详情侧滑 */}
        <SlideFrame
          title={
            JSON.stringify(detailsInfo) === '{}'
              ? '新建项目申请单详细信息'
              : '编辑项目申请单详细信息'
          }
          show={newDetailView}
          onClose={() => {
            this.closeNewDetailsSlideForm(false);
          }}
        >
          <CNewProjectDetails
            headerId={headerInfo.id}
            projectReqNumber={headerInfo.projectReqNumber}
            onClose={this.closeNewDetailsSlideForm}
            params={detailsParams}
            allInfo={allInfo} // 编辑时需要将所有数据都返回后端
            detailsAllParams={allInfo}
          />
        </SlideFrame>
        {/* 添加合同侧滑 */}
        <ListSelector
          visible={newContractView}
          onOk={this.handleAddContract}
          onCancel={this.closeContractModel}
          valueKey="contractHeaderId"
          labelKey="contractTypeName"
          selectorItem={selectorItem}
          selectedData={selectedContractData}
          extraParams={contractExtraParams}
          showRowClick
        />
        {/* 使用权限 */}
        <ListSelector
          visible={accessView}
          onCancel={this.hideAccessModal}
          onReturn={this.hideAccessModal}
          hideRowSelect
          labelKey={accessLabelKey}
          valueKey={accessValueKey}
          type={releaseIdsType}
          hideFooter
          showSelectTotal
          extraParams={releaseIdsExtraParams}
          selectedData={selectedAccessData}
          diyFooter
          showDetail
        />
        {/* 合同详情 */}
        <Modal
          title="合同详情"
          visible={contractShow}
          onCancel={() => {
            this.setState({ contractShow: false, newContractView: true });
          }}
          width="90%"
          bodyStyle={{
            maxHeight: '70vh',
            overflow: 'auto',
            padding: '0 10px',
          }}
          footer={null}
        >
          <ContractDetail id={contractId} isApprovePage />
        </Modal>
        {/* 客户详情 */}
        <Modal
          title="客户详情"
          visible={customerShow}
          onCancel={() => {
            this.setState({ customerShow: false });
          }}
          width="90%"
          bodyStyle={{
            maxHeight: '70vh',
            overflow: 'auto',
            padding: '0 10px',
          }}
          footer={null}
        >
          <CustomerDetails id={customerId} readOnly />
        </Modal>
        <Modal
          title="父项目申请单信息"
          visible={parentApplyShow}
          onCancel={() => {
            this.setState({ parentApplyShow: false });
          }}
          width="90%"
          bodyStyle={{
            maxHeight: '70vh',
            overflow: 'auto',
            padding: '0 10px',
            zIndex: 101,
          }}
          footer={null}
        >
          <Project match={{ params: { id: parentApplyId || '' } }} readOnly />
        </Modal>
        {readOnly ? <div /> : <BottomBar headerInfo={headerInfo} refresh={this.getHeaderInfo} />}
      </div>
    );
  }
}

ProjectApplyDetails.propTypes = {
  readOnly: PropTypes.bool, // 只读状态的页面（隐藏添加合同，编辑等操作）
  onlyChange: PropTypes.bool, // 只展示详细card处的变更btn
  match: PropTypes.object, //  存放headerId，为减少参数间改动，设置成match.params的形式
};

ProjectApplyDetails.defaultProps = {
  readOnly: false,
  onlyChange: false,
  match: { params: {} },
};

function mapStateToProps(state) {
  return {
    company: state.user.company,
    user: state.user.currentUser,
  };
}

const Project = connect(
  mapStateToProps,
  null,
  null,
  { withRef: true }
)(ProjectApplyDetails);
export default Project;
