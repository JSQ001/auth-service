import React, { Component } from 'react';
import { connect } from 'dva';
import config from 'config';
import SearchArea from 'widget/search-area';
//import baseService from 'share/base.service';
import { Input, Button, Badge, Divider, message, Popover, Row, Col, Modal } from 'antd';
import Table from 'widget/table';
const Search = Input.Search;
import moment from 'moment';
import ExcelExporter from 'widget/excel-exporter';
import ListSelector from 'widget/list-selector';
import FileSaver from 'file-saver';
import { routerRedux } from 'dva/router';
import service from './service';

class input_tax extends Component {
  constructor(props) {
    super(props);
    this.state = {
      status: {
        1001: { label: '编辑中', state: 'default' },
        1004: { label: '审批通过', state: 'success' },
        1002: { label: '审批中', state: 'processing' },
        1005: { label: '审批驳回', state: 'error' },
        1003: { label: '撤回', state: 'warning' },
        0: { label: '未知', state: 'warning' },
        2004: { label: '支付成功', state: 'success' },
        2003: { label: '支付中', state: 'processing' },
        2002: { label: '审核通过', state: 'success' },
        2001: { label: '审核驳回', state: 'error' },
      },
      searchFrom: [
        {
          type: 'list',
          id: 'companyId',
          label: '单据公司',
          colSpan: '6',
          listType: 'available_company',
          listExtraParams: { setOfBooksId: this.props.company.setOfBooksId },
          valueKey: 'id',
          labelKey: 'name',
          single: true,
        },
        {
          type: 'list',
          id: 'unitId',
          label: '单据部门',
          colSpan: '6',
          listType: 'department',
          labelKey: 'name',
          valueKey: 'departmentId',
          single: true,
        },
        {
          type: 'list',
          id: 'applyId',
          label: '申请人',
          colSpan: '6',
          listExtraParams: { setOfBooksId: this.props.company.setOfBooksId },
          listType: 'bgtUser',
          valueKey: 'id',
          labelKey: 'fullName',
          single: true,
        },
        {
          type: 'select',
          id: 'status',
          label: '状态',
          colSpan: '6',
          options: [
            { value: 1001, label: '编辑中' },
            { value: 1002, label: '审批中' },
            { value: 1003, label: '撤回' },
            { value: 1004, label: '审批通过' },
            { value: 1005, label: '审批驳回' },
          ],
          valueKey: 'value',
          labelkey: 'label',
        },
        {
          type: 'value_list',
          label: '业务大类',
          id: 'transferType',
          options: [],
          valueListCode: 'transferType',
          colSpan: 6,
        },
        {
          type: 'value_list',
          id: 'useType',
          label: '用途大类',
          valueListCode: 'useType',
          colSpan: 6,
          options: [],
        },
        {
          type: 'items',
          id: 'applyDate',
          colSpan: 6,
          items: [
            { type: 'date', id: 'applyDateFrom', label: '申请日期从' },
            { type: 'date', id: 'applyDateTo', label: '申请日期至' },
          ],
        },
        {
          type: 'select',
          label: '币种',
          id: 'currencyCode',
          colSpan: '6',
          options: [],
          method: 'get',
          getUrl: `${config.mdataUrl}/api/currency/rate/list`,
          listKey: 'records',
          getParams: {
            enable: true,
            setOfBooksId: this.props.company.setOfBooksId,
            tenantId: this.props.company.tenantId,
          },
          valueKey: 'currencyCode',
          labelKey: 'currencyCodeAndName',
          event: 'currencyCode',
        },
        {
          type: 'items',
          id: 'amount',
          colSpan: 6,
          items: [
            { type: 'input', id: 'amountFrom', label: '金额从' },
            { type: 'input', id: 'amountTo', label: '金额至' },
          ],
        },
        {
          type: 'select',
          id: 'reverseFlag',
          label: '反冲状态',
          colSpan: '6',
          options: [
            { value: 1001, label: '编辑中' },
            { value: 1002, label: '审批中' },
            { value: 1003, label: '撤回' },
            { value: 1004, label: '审批通过' },
            { value: 1005, label: '审批驳回' },
          ],
          valueKey: 'value',
          labelkey: 'label',
        },
        {
          type: 'items',
          id: 'auditorDate',
          colSpan: 6,
          items: [
            { type: 'date', id: 'auditorDateFrom', label: '审核日期从' },
            { type: 'date', id: 'auditorDateTo', label: '审核日期至' },
          ],
        },
        {
          type: 'input',
          id: 'remark',
          label: '备注',
          colSpan: 6,
        },
      ],
      columns: [
        {
          title: '单据编号',
          dataIndex: 'documentNumber',
          width: '120',
          render: (documentNumber, record) => {
            return (
              <Popover content={documentNumber}>
                <a onClick={() => this.handleLink(record)}>{documentNumber}</a>
              </Popover>
            );
          },
        },
        {
          title: '单据公司',
          dataIndex: 'companyName',
          width: '120',
          render: desc => <Popover content={desc}>{desc || '-'}</Popover>,
        },
        {
          title: '单据部门',
          dataIndex: 'departmentName',
          width: '100',
          render: desc => <Popover content={desc}>{desc || '-'}</Popover>,
        },
        {
          title: '申请人',
          dataIndex: 'fullName',
          width: '80',
          render: desc => <Popover content={desc}>{desc || '-'}</Popover>,
        },
        {
          title: '申请日期',
          dataIndex: 'createdDate',
          width: '120',
          render: createdDate => {
            return <span>{moment(createdDate).format('YYYY-MM-DD')}</span>;
          },
        },
        {
          title: '业务大类',
          dataIndex: 'transferTypeName',
          width: '120',
          render: typeName => {
            return <Popover content={typeName}>{typeName}</Popover>;
          },
        },
        {
          title: '币种',
          dataIndex: 'currencyCode',
          width: '100',
        },
        {
          title: '金额',
          dataIndex: 'amount',
          width: '100',
          render: amount => {
            return <span>{this.filterMoney(amount, 2)}</span>;
          },
        },
        {
          title: '状态',
          dataIndex: 'status',
          width: '100',
          render: value => (
            <Badge status={this.state.status[value].state} text={this.state.status[value].label} />
          ),
        },
        {
          title: '审核日期',
          dataIndex: 'lastUpdatedDate',
          width: '120',
          render: lastUpdatedDate => {
            return <span>{moment(lastUpdatedDate).format('YYYY-MM-DD')}</span>;
          },
        },
        {
          title: '反冲状态',
          dataIndex: 'reverseFlag',
          width: '100',
        },
        {
          title: '备注',
          dataIndex: 'description',
          render: remarks => {
            return <Popover content={remarks}>{remarks}</Popover>;
          },
        },
      ],

      exportColumns: [
        { title: '单据编号', dataIndex: 'documentNumber' },
        { title: '单据公司', dataIndex: 'companyName' },
        { title: '单据部门', dataIndex: 'departmentName' },
        { title: '申请人', dataIndex: 'fullName' },
        { title: '申请日期', dataIndex: 'createdDate' },
        { title: '业务大类', dataIndex: 'transferTypeName' },
        { title: '币种', dataIndex: 'currencyCode' },
        { title: '金额', dataIndex: 'amount' },
        { title: '状态', dataIndex: 'statusName' },
        { title: '审核日期', dataIndex: 'lastUpdatedDate' },
        { title: '反冲状态', dataIndex: 'reverseFlag' },
        { title: '备注', dataIndex: 'description' },
      ],
      btLoading: false,
      excelVisible: false,
      loading: true,
      data: [],
      excelVisible: false,
      pagination: {
        total: 0,
        showSizeChanger: true,
        showQuickJumper: true,
      },
      page: 0,
      pageSize: 10,

      searchParam: {},
    };
  }

  componentWillMount = () => {
    this.getList();
  };

  //搜索
  searh = params => {
    if (params.companyId && params.companyId[0]) {
      params.companyId = params.companyId[0];
    }
    if (params.unitId && params.unitId[0]) {
      params.unitId = params.unitId[0];
    }
    if (params.applyId && params.applyId[0]) {
      params.applyId = params.applyId[0];
    }
    this.setState(
      {
        loading: true,
        page: 0,
        searchParam: params,
      },
      () => {
        this.getList();
      }
    );
  };
  //查询
  getList = () => {
    let { page, pageSize, searchParam } = this.state;
    let params = searchParam;

    params.page = page;
    params.size = pageSize;
    if (searchParam.applyDateFrom) {
      params.applyDateFrom = moment(searchParam.applyDateFrom).format('YYYY-MM-DD');
    }
    if (searchParam.applyDateTo) {
      params.applyDateTo = moment(searchParam.applyDateTo).format('YYYY-MM-DD');
    }
    if (searchParam.auditorDateFrom) {
      params.auditorDateFrom = moment(searchParam.auditorDateFrom).format('YYYY-MM-DD');
    }
    if (searchParam.auditorDateTo) {
      params.auditorDateTo = moment(searchParam.auditorDateTo).format('YYYY-MM-DD');
    }
    params.tenantId = this.props.company.tenantId;

    // 数据组装  并开始查询、
    service
      .getList(params)
      .then(res => {
        if (res.status == 200) {
          this.setState({
            data: res.data,
            loading: false,
            pagination: {
              total: Number(res.headers['x-total-count'])
                ? Number(res.headers['x-total-count'])
                : 0,
              current: page + 1,
              onChange: this.onChangeCheckedPage,
              onShowSizeChange: this.onShowSizeChange,
              showSizeChanger: true,
              showQuickJumper: true,
              showTotal: total => `共搜到 ${total} 条数据`,
              searchParam: params,
            },
          });
        }
      })
      .catch(e => {
        message.error('加载数据失败');
      });
  };

  //清空
  clear = () => {
    this.setState(
      {
        loading: true,
        page: 0,
        searchParam: {},
      },
      () => {
        this.getList();
      }
    );
  };
  // 搜索单号
  onDocumentSearch = value => {
    this.setState(
      {
        loading: true,
        page: 0,
        searchParam: { ...this.state.searchParam, documentNumber: value },
      },
      () => {
        this.getList();
      }
    );
  };
  //导出
  onExportClick = () => {
    this.setState({
      btLoading: true,
      excelVisible: true,
    });
  };
  //导出取消
  onExportCancel = () => {
    this.setState({
      btLoading: false,
      excelVisible: false,
    });
  };
  //导出
  export = result => {
    let hide = message.loading('正在生成文件，请等待......');

    let setOfBooksId = this.props.company.setOfBooksId;

    const exportParams = this.state.searchParam;
    exportParams.tenantId = this.props.company.tenantId;
    service
      .export(result, exportParams)
      .then(res => {
        console.log(res);
        if (res.status === 200) {
          message.success('操作成功');
          0; //content-disposition 在response.headers 里面没有。
          let fileName = '进项税业务单.xlsx'; //res.headers['content- '].split('filename=')[1];
          let f = new Blob([res.data]);
          FileSaver.saveAs(f, decodeURIComponent(fileName));
          this.setState({
            btLoading: false,
          });
          hide();
        }
      })
      .catch(e => {
        alert(e);
        console.log('下载失败' + this.e);
        message.error('下载失败，请重试!');
        this.setState({
          btLoading: false,
        });
        hide();
      });
  };

  /**
   * 切换分页
   */
  onChangeCheckedPage = page => {
    if (page - 1 !== this.state.page) {
      this.setState(
        {
          loading: true,
          page: page - 1,
        },
        () => {
          this.getList();
        }
      );
    }
  };
  /**
   * 切换每页显示的条数
   */
  onShowSizeChange = (current, pageSize) => {
    this.setState(
      {
        loading: true,
        page: current - 1,
        pageSize,
      },
      () => {
        this.getList();
      }
    );
  };
  //超链接跳转
  handleLink(record) {
    this.props.dispatch(
      routerRedux.replace({
        //pathname: `/pre-payment/my-pre-payment/pre-payment-detail/${record.id}/preFinalQuery`,
        pathname: `/financial-view/input-tax-finance/${record.id}/${record.transferType}/detil`,
      })
    );
  }

  render() {
    let {
      searchFrom,
      btLoading,
      pagination,
      excelVisible,
      columns,
      exportColumns,
      data,
      loading,
    } = this.state;

    return (
      <div>
        <SearchArea
          searchForm={searchFrom}
          submitHandle={this.searh}
          clearHandle={this.clear}
          maxLength={4}
        />
        <div className="divider" />
        <div className="table-header">
          <div className="table-header-buttons">
            <Row>
              <Col span={18}>
                <Button loading={btLoading} type="primary" onClick={this.onExportClick}>
                  导出进项税业务单
                </Button>
              </Col>
              <Col span={6}>
                <Search
                  placeholder="请输入进项税业务单单号"
                  onSearch={this.onDocumentSearch}
                  enterButton
                />
              </Col>
            </Row>
          </div>
        </div>
        <Table
          scroll={{ x: 1850 }}
          rowKey={record => record['id']}
          columns={columns}
          size="middle"
          bordered
          loading={loading}
          pagination={pagination}
          dataSource={data}
        />
        {/* 导出 */}
        <ExcelExporter
          visible={excelVisible}
          onOk={this.export}
          columns={exportColumns}
          canCheckVersion={false}
          fileName={'进项税业务单'}
          onCancel={this.onExportCancel}
          excelItem={'INPUT_TAX_FINANCIAL_QUERY'}
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
)(input_tax);
