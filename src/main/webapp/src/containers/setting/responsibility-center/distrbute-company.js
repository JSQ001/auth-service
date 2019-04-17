import React, { Component } from 'react';
import { Button, Icon, Checkbox, message } from 'antd';
import BasicInfo from 'widget/basic-info';
import { connect } from 'dva';
import { routerRedux } from 'dva/router';
import Table from 'widget/table';
import ListSelector from 'components/Widget/list-selector';
import ResponsibilityService from 'containers/setting/responsibility-center/responsibility-service';
import config from 'config';

class BatchSingleCompany extends Component {
  constructor(props) {
    super(props);
    this.state = {
      infoData: {},
      infoList: [
        {
          type: 'input',
          id: 'setOfBooksName',
          isRequired: true,
          label: '账套',
          colSpan: 6,
        },
        {
          type: 'input',
          id: 'responsibilityCenterCode',
          isRequired: true,
          label: '责任中心代码',
          colSpan: 6,
        },
        {
          type: 'input',
          id: 'responsibilityCenterName',
          isRequired: true,
          label: '责任中心名称',
          colSpan: 6,
        },
        {
          type: 'switch',
          id: 'enabled',
          isRequired: true,
          label: '状态',
          colSpan: 6,
        },
      ],
      responsibilityCenterId: this.props.match.params.responsibilityCenterId,
      page: 0,
      size: 10,
      pagination: {
        showSizeChanger: true,
        showQuickJumper: true,
        showTotal: total => `一共${total}条数据`,
      },
      columns: [
        { title: '公司代码', dataIndex: 'companyCode', align: 'center' },
        { title: '公司名称', dataIndex: 'companyName', align: 'center' },
        { title: '公司类型', dataIndex: 'companyType', align: 'center' },
        {
          title: '启用',
          dataIndex: 'enabled',
          align: 'center',
          render: (enabled, record, index) => {
            return <Checkbox checked={enabled} onChange={e => this.onIsEnabledChange(e, record)} />;
          },
        },
      ],
      isLoading: false,
      //table表dataSource
      companyData: [],
      //公司模态框可见
      companyVisible: false,
      //公司模态框样式
      selectorItem: {
        title: '批量分配公司',
        url: `${config.mdataUrl}/api/responsibilityCenter/company/assign/filter`,
        searchForm: [
          { type: 'input', id: 'companyCode', label: '公司代码' },
          { type: 'input', id: 'companyName', label: '公司名称' },
          { type: 'input', id: 'companyCodeFrom', label: '公司代码从' },
          { type: 'input', id: 'companyCodeTo', label: '公司代码至' },
        ],
        columns: [
          { title: '公司代码', dataIndex: 'companyCode' },
          { title: '公司名称', dataIndex: 'name' },
          { title: '公司类型', dataIndex: 'companyTypeName' },
        ],
        key: 'id',
      },
      //新增模态框可见
      isVisibleForFrame: false,
      //新增公司数据
      modelData: [],
    };
  }

  componentDidMount = () => {
    this.getCompanyData();
    this.getCurDimensionValue();
  };

  //  获取当前责任中心详情
  getCurDimensionValue = () => {
    const id = this.state.responsibilityCenterId;
    ResponsibilityService.getCurrentDimensionValue(id)
      .then(res => {
        this.setState({ infoData: res.data });
      })
      .catch(err => {
        message.error('查询详情失败:' + err.response.data.message);
      });
  };
  //获取公司数据
  getCompanyData = () => {
    const { page, size, responsibilityCenterId, pagination } = this.state;
    let params = { page, size, responsibilityCenterId };
    this.setState({ isLoading: true });
    ResponsibilityService.searchCompanyBatch(params)
      .then(res => {
        pagination.total = Number(res.headers['x-total-count']);
        this.setState({
          companyData: res.data,
          isLoading: false,
          pagination,
        });
      })
      .catch(err => {
        this.setState({ isLoading: false });
        message.error('获取公司数据失败' + err);
      });
  };

  //分页
  tablePageChange = pagination => {
    this.setState(
      {
        page: pagination.current - 1,
        size: pagination.pageSize || 10,
      },
      () => {
        this.getCompanyData();
      }
    );
  };

  //返回上一页
  onBackClick = e => {
    e.preventDefault();
    this.props.dispatch(
      routerRedux.replace({
        pathname: `/admin-setting/responsibility-center/responsibility-center`,
      })
    );
  };

  //是否启用
  onIsEnabledChange = (e, record) => {
    let params = [];
    params.push({
      id: record.id,
      enabled: e.target.checked,
    });
    ResponsibilityService.enableCompany(params)
      .then(res => {
        this.getCompanyData();
        message.success('修改状态成功');
      })
      .catch(err => {
        message.error(err.response.data.message);
      });
  };

  //批量分配公司
  handleBatch = () => {
    this.setState({ companyVisible: true });
  };
  onCompanyCancel = () => {
    this.setState({ companyVisible: false });
  };
  onCompanyOk = value => {
    const params = [];
    value.result.map(item => {
      params.push({
        companyId: item.id,
        companyCode: item.companyCode,
        enabled: item.enabled,
        responsibilityCenterId: this.state.responsibilityCenterId,
      });
    });
    ResponsibilityService.companyBatch(params)
      .then(res => {
        message.success('分配成功');
        this.getCompanyData();
        this.setState({
          companyVisible: false,
          selectedRowKeys: [],
        });
      })
      .catch(err => {
        this.setState({
          companyVisible: false,
          selectedRowKeys: [],
        });
        message.error(err.response.data.message);
      });
  };

  render() {
    const {
      infoList,
      infoData,
      pagination,
      columns,
      isLoading,
      companyData,
      companyVisible,
      selectorItem,
      responsibilityCenterId,
    } = this.state;
    return (
      <div>
        <BasicInfo infoList={infoList} infoData={infoData} isHideEditBtn={true} />
        <Button type="primary" style={{ margin: '20px' }} onClick={this.handleBatch}>
          分配公司
        </Button>
        <Table
          columns={columns}
          pagination={pagination}
          loading={isLoading}
          dataSource={companyData}
          size="middle"
          bordered
          rowKey={record => record.id}
          onChange={this.tablePageChange}
        />
        <p style={{ marginBottom: '20px' }}>
          <a onClick={this.onBackClick}>
            <Icon type="rollback" />返回
          </a>
        </p>
        <ListSelector
          visible={companyVisible}
          onCancel={this.onCompanyCancel}
          onOk={this.onCompanyOk}
          selectorItem={selectorItem}
          extraParams={{ responsibilityCenterId }}
          single={false}
          showSelectTotal={true}
        />
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
export default connect(
  mapStateToProps,
  null,
  null,
  { withRef: true }
)(BatchSingleCompany);
