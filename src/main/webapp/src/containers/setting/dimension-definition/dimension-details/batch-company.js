import React, { Component } from 'react';
import { Row, Col, Badge, Button, Icon, Checkbox, message } from 'antd';
import SlideFrame from 'widget/slide-frame';
import { connect } from 'dva';
import { routerRedux } from 'dva/router';
import Table from 'widget/table';
import ListSelector from 'components/Widget/list-selector';
import BasicInfo from 'widget/basic-info';

import config from 'config';
import dimensionValueService from './dimension-value-service';

class BatchSingleCompany extends Component {
  constructor(props) {
    super(props);
    this.state = {
      //当前维值的数据
      curTypeList: {},
      //当前账套Id
      setOfBooksId: this.props.match.params.setOfBooksId,
      //当前维度Id
      dimensionId: this.props.match.params.dimensionId,
      //当前维值Id
      dimensionItemId: this.props.match.params.dimensionItemId,
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
        url: `${config.mdataUrl}/api/dimension/item/assign/company/filter`,
        searchForm: [
          { type: 'input', id: 'companyCode', label: '公司代码' },
          { type: 'input', id: 'companyName', label: '公司名称' },
          { type: 'input', id: 'companyCodeFrom', label: '公司代码从' },
          { type: 'input', id: 'companyCodeTo', label: '公司代码至' },
        ],
        columns: [
          { title: '公司代码', dataIndex: 'companyCode' },
          { title: '公司名称', dataIndex: 'name' },
          {
            title: '公司类型',
            dataIndex: 'companyTypeName',
            render: value => (value ? value : '-'),
          },
        ],
        key: 'id',
      },
      //新增模态框可见
      isVisibleForFrame: false,
      //新增公司数据
      modelData: [],
      infoList: [
        {
          type: 'input',
          id: 'dimensionItemCode',
          isRequired: true,
          label: '维值代码',
        },
        {
          type: 'input',
          id: 'dimensionItemName',
          isRequired: true,
          label: '维值名称',
        },
        {
          type: 'switch',
          id: 'enabled',
          isRequired: true,
          label: '状态',
        },
      ],
    };
  }

  componentDidMount = () => {
    this.getCompanyData();
    this.getCurDimensionValue();
  };

  //获取当前维值详情
  getCurDimensionValue = () => {
    dimensionValueService
      .getCurrentDimensionValue(this.state.dimensionItemId)
      .then(res => {
        this.setState({
          curTypeList: { ...res.data['dimensionItem'] },
        });
      })
      .catch(err => {
        message.error('查询详情失败:' + err.response.data.message);
      });
  };
  //获取公司数据
  getCompanyData = () => {
    const { page, size, dimensionItemId, pagination } = this.state;
    let params = { page, size, dimensionItemId };
    this.setState({ isLoading: true });
    dimensionValueService
      .getCompanyList(params)
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
        this.getDetailsValue();
      }
    );
  };

  //返回上一页
  onBackClick = e => {
    const { dimensionId } = this.state;
    e.preventDefault();
    this.setState({
      curTypeList: {},
      dimensionItemId: null,
    });
    this.props.dispatch(
      routerRedux.replace({
        pathname: `/admin-setting/dimension-definition/dimension-details/${dimensionId}`,
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
    dimensionValueService
      .toEnableTheCompany(params)
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
        dimensionItemId: this.state.dimensionItemId,
      });
    });
    dimensionValueService
      .addNewCompanyData(params)
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
      curTypeList,
      infoList,
      pagination,
      columns,
      isLoading,
      companyData,
      companyVisible,
      selectorItem,
      dimensionItemId,
    } = this.state;
    return (
      <div>
        <BasicInfo infoList={infoList} infoData={curTypeList} isHideEditBtn={true} />
        <Button type="primary" style={{ margin: '20px 0' }} onClick={this.handleBatch}>
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
        <div style={{ margin: '20px 0' }}>
          <a onClick={this.onBackClick}>
            <Icon type="rollback" />返回
          </a>
        </div>
        <ListSelector
          visible={companyVisible}
          onCancel={this.onCompanyCancel}
          onOk={this.onCompanyOk}
          selectorItem={selectorItem}
          extraParams={{ dimensionItemId }}
          single={false}
          showSelectTotal={true}
        />
      </div>
    );
  }
}

export default connect(
  null,
  null,
  null,
  { withRef: true }
)(BatchSingleCompany);
