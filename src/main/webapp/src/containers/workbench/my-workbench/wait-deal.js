import React, { Component } from 'react';
import { Radio, message, Spin } from 'antd';
import config from 'config';
import { connect } from 'dva';
import SearchArea from 'widget/search-area';
import moment from 'moment';
import WorkTap from './work-tap';
import TemporaryTap from './temporary-tap';
import Service from './service';

class WaitDeal extends Component {
  constructor(props) {
    super(props);
    this.state = {
      searchForm: [
        {
          type: 'list',
          id: 'companyId',
          label: '单据公司',
          colSpan: 6,
          listType: 'available_company',
          listExtraParams: { setOfBooksId: props.company.setOfBooksId },
          valueKey: 'id',
          labelKey: 'name',
          single: true,
        },
        {
          type: 'list',
          id: 'employeeId',
          label: '申请人',
          colSpan: 6,
          listExtraParams: { setOfBooksId: props.company.setOfBooksId },
          listType: 'bgtUser',
          valueKey: 'id',
          labelKey: 'fullName',
          single: true,
        },
        {
          type: 'items',
          colSpan: 6,
          id: 'abc',
          items: [
            { type: 'date', id: 'dateFrom', colSpan: 3, label: '入池日期从' },
            { type: 'date', id: 'dateTo', colSpan: 3, label: '入池日期至' },
          ],
        },
        {
          type: 'list',
          colSpan: 6,
          id: 'businessTypeId',
          label: '业务类型',
          listType: 'bussiness_type',
          labelKey: 'businessTypeName',
          valueKey: 'id',
          single: true,
        },
        {
          type: 'select',
          colSpan: 6,
          id: 'documentTypeId',
          label: '单据类型',
          options: [],
          method: 'get',
          getUrl: `${config.expenseUrl}/api/invoice/type/query/for/invoice?tenantId=${
            props.company.tenantId
          }&setOfBooksId=${props.company.setOfBooksId}`,
          labelKey: 'invoiceTypeName',
          valueKey: 'id',
        },
        {
          type: 'list',
          colSpan: 6,
          id: 'currentNodeId',
          label: '当前业务节点',
          listType: 'business_node',
          labelKey: 'businessNodeName',
          valueKey: 'id',
          single: true,
        },
        {
          type: 'list',
          colSpan: 6,
          id: 'lastNodeId',
          label: '上一业务节点',
          listType: 'business_node',
          labelKey: 'businessNodeName',
          valueKey: 'id',
          single: true,
        },
        {
          type: 'list',
          colSpan: 6,
          id: 'lastOperatorId',
          label: '上一操作人',
          listExtraParams: { setOfBooksId: props.company.setOfBooksId },
          listType: 'bgtUser',
          valueKey: 'id',
          labelKey: 'fullName',
          single: true,
        },
        {
          type: 'list',
          colSpan: 6,
          id: 'lastWorkTeamId',
          label: '上一操作人工作组',
          listType: 'work_team',
          valueKey: 'id',
          labelKey: 'workTeamName',
          single: true,
        },
      ],
      workTableData: [],
      workTableTotalNum: 0,
      workCurrentPage: 0,
      workSize: 10,
      isStartWork: false, // 开始工作/结束工作
      isAutoCheckOrder: false, // 自动阅单/取消自动阅单
      temporaryTableData: [],
      temporaryTableTotalNum: 0,
      temporaryCurrentPage: 0,
      temporarySize: 10,
      whichTap: 'WORKING', // WORKING(工作区)PENDING(暂存区)LOOKUP(查看区)
      loading: false,
    };
  }

  componentDidMount() {
    console.log(this.props);
    this.setState({ whichTap: this.props.type });
    this.getInitStatus();
    this.getList();
  }

  /**
   * 初始化工作状态
   */
  getInitStatus = () => {
    Service.getInitStatus()
      .then(res => {
        if (res && res.data) {
          this.setState({
            isStartWork: !!res.data.workStatus,
            isAutoCheckOrder: !!res.data.automaticFlag,
          });
        }
      })
      .catch(() => {
        message.warn('初始化报错');
      });
  };

  /**
   * 切换工作区临时区
   */
  changeTap = e => {
    this.setState({ whichTap: e.target.value });
  };

  /**
   * 搜索
   */
  search = params => {
    const newParams = {
      companyId: params.companyId && params.companyId[0],
      employeeId: params.employeeId && params.employeeId[0],
      dateFrom: params.dateFrom && params.dateFrom.format('YYYY-MM-DD'),
      dateTo: params.dateTo && params.dateTo.format('YYYY-MM-DD'),
      businessTypeId: params.businessTypeName && params.businessTypeName[0],
      documentTypeId: params.documentTypeId,
      currentNodeId: params.currentNodeId && params.currentNodeId[0],
      lastNodeId: params.lastNodeId && params.lastNodeId[0],
      lastOperatorId: params.lastOperatorId && params.lastOperatorId[0],
      lastWorkTeamId: params.lastWorkTeamId && params.lastWorkTeamId[0],
    };

    this.getList(newParams);
  };

  /**
   * 获取表格数据
   */
  getList = (value = {}) => {
    const { whichTap, workCurrentPage, workSize, temporaryCurrentPage, temporarySize } = this.state;
    const page = whichTap === 'WORKING' ? workCurrentPage : temporaryCurrentPage;
    const size = whichTap === 'WORKING' ? workSize : temporarySize;
    const params = { ...value, page, size, type: whichTap };
    this.setState({ loading: true });

    Service.queryWorkBenchTableData(params)
      .then(data => {
        const total = Number(data.headers['x-total-count']) || 0;
        if (data && data.data && data.data.length >= 0) {
          const newData = data.data.map(item => {
            return Object.assign(item, {
              lastOperatorDate: item.lastOperatorDate
                ? moment(item.lastOperatorDate).format('YYYY-MM-DD')
                : '',
              enterDate: item.enterDate ? moment(item.enterDate).format('YYYY-MM-DD') : '',
            });
          });
          if (whichTap === 'WORKING') {
            this.setState({
              workTableData: newData,
              workTableTotalNum: total,
              loading: false,
            });
          } else {
            this.setState({
              temporaryTableData: newData,
              temporaryTableTotalNum: total,
              loading: false,
            });
          }
        }
      })
      .catch(err => message.error(err.response.data.message));
  };

  /**
   * 显示或隐藏loading
   */
  isShowLoading = bool => {
    this.setState({
      loading: !!bool,
    });
  };

  /**
   * 工作区分页回调
   */
  workTapCallBack = (page, size) => {
    this.setState(
      {
        workCurrentPage: page,
        workSize: size,
      },
      () => {
        this.getList();
      }
    );
  };

  /**
   * 工作区 开始工作/是否自动阅单 状态修改
   */
  workChangeStateCB = (isStartWork, isAutoCheckOrder) => {
    this.setState({
      isStartWork,
      isAutoCheckOrder,
    });
  };

  /**
   * 临时取分页回调
   */
  temporaryTapCallBack = (page, size) => {
    this.setState(
      {
        temporaryCurrentPage: page,
        temporarySize: size,
      },
      () => {
        this.getList();
      }
    );
  };

  /**
   * 设置搜索单号
   */
  setDocumentNumberCB = documentNumber => {
    this.getList({ documentNumber });
  };

  render() {
    const {
      searchForm,
      workTableData,
      temporaryTableData,
      whichTap,
      workTableTotalNum,
      workCurrentPage,
      workSize,
      isStartWork,
      isAutoCheckOrder,
      temporaryTableTotalNum,
      temporaryCurrentPage,
      temporarySize,
      loading,
    } = this.state;

    return (
      <div style={{ paddingTop: '10px' }}>
        {loading && (
          <div
            style={{
              position: 'fixed',
              left: '55%',
              top: '50%',
              zIndex: 10000,
            }}
          >
            <Spin size="large" />
          </div>
        )}
        <SearchArea cl submitHandle={this.search} searchForm={searchForm} maxLength={4} />
        <Radio.Group value={whichTap} onChange={this.changeTap} style={{ marginTop: 20 }}>
          <Radio.Button value="WORKING">工作区</Radio.Button>
          <Radio.Button value="PENDING">暂挂区</Radio.Button>
        </Radio.Group>
        {whichTap === 'WORKING' ? (
          <WorkTap
            dataSource={workTableData}
            workTableTotalNum={workTableTotalNum}
            workCurrentPage={workCurrentPage}
            workSize={workSize}
            isStartWork={isStartWork}
            isAutoCheckOrder={isAutoCheckOrder}
            workTapCallBack={this.workTapCallBack}
            workChangeStateCB={this.workChangeStateCB}
            setDocumentNumberCB={this.setDocumentNumberCB}
            isShowLoading={this.isShowLoading}
          />
        ) : (
          <TemporaryTap
            dataSource={temporaryTableData}
            temporaryTableTotalNum={temporaryTableTotalNum}
            temporaryCurrentPage={temporaryCurrentPage}
            temporarySize={temporarySize}
            temporaryTapCallBack={this.temporaryTapCallBack}
            setDocumentNumberCB={this.setDocumentNumberCB}
            isShowLoading={this.isShowLoading}
          />
        )}
      </div>
    );
  }
}

function mapStateToProps(state) {
  return {
    user: state.user.currentUser,
    company: state.user.company,
  };
}

export default connect(mapStateToProps)(WaitDeal);
