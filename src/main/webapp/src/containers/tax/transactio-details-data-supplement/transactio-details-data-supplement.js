/* eslint-disable */
import React, { Component } from 'react';
import SearchArea from 'widget/search-area';
import { Button, message, Modal, Form, DatePicker, Popover } from 'antd';
import SlideFrame from 'widget/slide-frame';
import config from 'config';
import moment from 'moment';
import { connect } from 'dva';
import { routerRedux } from 'dva/router';
import Table from 'widget/table';
import jobService from 'containers/job/job.service';
import Service from './transactio-details-data-supplement.service';
import NewDetailsData from './new-transactio-details-data-supplement';
const confirm = Modal.confirm;
const { MonthPicker, RangePicker } = DatePicker;
const monthFormat = 'YYYY/MM';
class TransactioDetailsDataSupplement extends Component {
  constructor(props) {
    super(props);
    this.state = {
      searchForm: [
        {
          type: 'value_list',
          valueListCode: 'TAX_SOURCE_SYSTEM',
          colSpan: 6,
          // id为传到后台的字段
          id: 'sourceSystem',
          label: '来源系统',
          options: [],
        },
        {
          type: 'input',
          id: 'transNum',
          placeholder: '请输入',
          label: '交易流水号',
          colSpan: 6,
        },
        {
          type: 'list',
          id: 'clientAcc',
          colSpan: 6,
          listType: 'customer_information_query',
          labelKey: 'clientName',
          valueKey: 'id',
          event: 'clientNumber',
          single: true,
          listExtraParams: {},
          label: '客户名称' /*客户名称*/,
        },
        {
          type: 'select',
          key: 'currency',
          id: 'currencyCode',
          label: '币种',
          getUrl: `${config.mdataUrl}/api/currency/rate/company/standard/currency/getAll`,
          getParams: { setOfBooksId: this.props.company.setOfBooksId },
          options: [],
          method: 'get',
          labelKey: '${currency}-${currencyName}',
          valueKey: 'currency',
          colSpan: 6,
        },
        {
          type: 'list',
          colSpan: 6,
          id: 'org',
          label: '交易机构',
          listType: 'company_detail',
          valueKey: 'id',
          labelKey: 'name',
          options: [],
          listExtraParams: { setOfBooksId: this.props.company.setOfBooksId },
          single: true,
          event: 'COMPANY_ID',
          allowClear: true,
        },
        {
          type: 'input',
          id: 'batchId',
          placeholder: '请输入',
          label: '批次号',
          colSpan: 6,
        },
        {
          type: 'items',
          id: 'date',
          items: [
            { type: 'date', id: 'transDateFrom', label: '交易日期从' },
            { type: 'date', id: 'transDateTo', label: '交易日期至' },
          ],
          colSpan: 6,
        },
      ],
      // 分页代码1
      pagination: {
        total: 0,
        onChange: this.onChangePager,
        current: 1,
        onShowSizeChange: this.onChangePageSize,
        showSizeChanger: true,
        showQuickJumper: true,
        pageSize: 10,
        pageSizeOptions: ['5', '10', '20', '50', '100'],
        showTotal: (total, range) =>
          this.$t(
            { id: 'common.show.total' },
            { range0: `${range[0]}`, range1: `${range[1]}`, total }
          ),
      },
      visibel: false,
      model: {},
      columns: [
        {
          title: '来源系统',
          dataIndex: 'sourceSystemName',
          align: 'center',
          width: 200,
        },
        {
          title: '批次号',
          dataIndex: 'batchId',
          align: 'center',
          width: 200,
        },
        {
          title: '交易流水号',
          dataIndex: 'transNum',
          align: 'center',
          width: 200,
        },
        {
          title: '交易机构',
          dataIndex: 'org',
          align: 'center',
          width: 200,
        },
        {
          title: '客户编号',
          dataIndex: 'clientAcc',
          align: 'center',
          width: 200,
        },
        {
          title: '客户名称',
          dataIndex: 'taxpayerName',
          align: 'center',
          width: 200,
        },
        {
          title: '交易币种',
          dataIndex: 'currencyCode',
          align: 'center',
          width: 200,
        },
        {
          title: '原币种金额',
          dataIndex: 'amount',
          align: 'center',
          width: 200,
        },
        {
          title: '数据创建方式',
          dataIndex: 'dataTypeName',
          align: 'center',
          width: 200,
        },
        {
          title: '价税分离状态',
          dataIndex: 'processFlagName',
          align: 'center',
          width: 200,
        },
        {
          title: '交易日期',
          dataIndex: 'transDate',
          align: 'center',
          width: 200,
          render: recode => {
            return (
              <Popover content={moment(recode).format('YYYY-MM-DD')}>
                {recode ? moment(recode).format('YYYY-MM-DD') : ''}
              </Popover>
            );
          },
        },
        {
          title: '操作',
          dataIndex: 'operation',
          align: 'center',
          render: (value, record) => (
            <div>
              <a onClick={() => this.edit(record)}>编辑</a>
            </div>
          ),
          width: 100,
        },
        {
          title: '错误信息',
          dataIndex: 'errorMessage',
          align: 'center',
          width: 200,
        },
      ],
      searchParams: {},
      model: {},
      taxCategory: {},
      ruleParameterTypeArray: [], //值列表
      importSysId: '',
      data: [],
      selectedKey: [],
      id: props.match.params.id,
      importSys: [
        {
          type: 'value_list',
          id: 'importSys',
          valueListCode: 2023,
          options: [],
        },
      ],
      lsVisible: false,
    };
    this.showConfirm = this.showConfirm.bind(this);
  }
  componentWillMount() {
    this.getList();
    this.setColumns();
  }

  // 获得数据
  getList() {
    const { pagination, searchParams } = this.state;
    const params = { ...searchParams };
    params.page = pagination.current - 1;
    params.size = pagination.pageSize;
    Service.getCangeRecord(params)
      .then(response => {
        response.data.map(item => {
          item.key = item.id;
        });
        (pagination.total = Number(response.headers['x-total-count']) || 0),
          this.setState({
            data: response.data,
            loading: false,
            pagination,
          });
      })
      .catch(() => {});
  }
  // 动态创建columns
  setColumns = () => {
    const { columns } = this.state;
    Service.getColumns().then(res => {
      if (res.data && res.data.length) {
        columns.splice(
          2,
          0,
          ...res.data.map(item => {
            return {
              dataIndex: item.colName.replace('_f', 'F'),
              title: item.dimensionName,
              key: item.dimensionId,
              width: 100,
              align: 'center',
              render: (value, record) => (
                <span>
                  {value}-{record[item.colName.replace('_f', 'F') + 'Name']}
                </span>
              ),
            };
          })
        );
        this.setState({ columns });
      }
    });
  };
  // 每页多少条
  onChangePageSize = (page, pageSize) => {
    const { pagination } = this.state;
    pagination.pageSize = pageSize;
    pagination.page = page;
    this.setState({ pagination }, () => {
      this.getList();
    });
  };

  // 分页点击
  onChangePager = page => {
    const { pagination } = this.state;
    pagination.current = page;
    this.setState({ pagination, loading: true }, () => {
      this.getList();
    });
  };
  //编辑
  edit = record => {
    this.setState({ model: JSON.parse(JSON.stringify(record)), visibel: true });
  };
  // 关闭侧滑页面
  close = flag => {
    this.setState({ visibel: false, model: {} }, () => {
      flag && this.getList();
    });
  };

  handleCancel = () => {
    // eslint-disable-next-line no-unused-expressions
    this.props.onClose && this.props.onClose(true);
  };

  /**
   * 获取值列表
   * @param code :值列表代码
   * @param name :值列表名称
   */
  getValueList(code, name) {
    name.splice(0, name.length);
    this.getSystemValueList(code).then(response => {
      response.data.values.map(item => {
        let option = {
          key: item.value,
          id: item.value,
          value: item.name,
        };
        name.addIfNotExist(option);
      });
      this.setState({
        name,
      });
    });
    return;
  }
  // 获取维值下拉列表
  getTaxQuaifation() {
    // eslint-disable-next-line prefer-const
    let taxQuaifationOptions = [];
    Service.getSystemValueList().then(res => {
      let taxClientTypeOptions = [];
      res.data.map(data => {
        taxClientTypeOptions.push({
          label: data.dimensionName,
          value: data.dimensionName,
          key: data.dimensionName,
          id: data.dimensionId,
        });
      });
      this.setState({
        taxClientTypeOptions,
      });
    });
  }
  showConfirm() {
    confirm({
      title: '提示',
      content: '请求提交成功，是否跳转请求运行监控界面？',
      onOk: () => {
        this.setState({
          ruleParameterTypeArray: [],
          lsVisible: false,
        });
        this.props.dispatch(
          routerRedux.push({
            pathname: '/job/job-log/job-log',
          })
        );
      },
      onCancel: () => {
        this.setState({
          ruleParameterTypeArray: [],
          lsVisible: false,
        });
      },
    });
  }

  //搜索
  handleSearch = params => {
    let pagination = this.state.pagination;
    pagination.page = 0;
    pagination.current = 1;

    this.setState(
      {
        searchParams: params,
        loading: true,
        pagination,
      },
      () => {
        this.getList();
      }
    );
  };
  //重置
  reset = () => {};

  onCancel = () => {
    this.setState({
      ruleParameterTypeArray: [],
      lsVisible: false,
    });
  };

  // 立即运行
  run = id => {
    jobService
      .runJobInfo(id)
      .then(res => {
        if (res.data.code === 200) {
          this.showConfirm();
        } else {
          message.error(this.$t({ id: 'common.operate.filed' } /*操作失败*/) + '!' + res.data.msg);
        }
      })
      .catch(e => {
        message.error(
          this.$t({ id: 'common.operate.filed' } /*操作失败*/) + '!' + e.response.data.message
        );
      });
  };
  submit = () => {
    const { selectedKey } = this.state;
    let data1 = [];
    selectedKey.map(res => {
      data1.push({
        id: res,
      });
    });
    Service.submitData(data1)
      .then(res => {
        console.log(res.data);
        message.success(res.data.resultMessage);
        this.setState({ selectedKey: [] });
        this.getList();
      })
      .catch(err => message.warning(err.response.data.message));
  };

  submitClick = () => {
    const { selectedKey } = this.state;
    if (!selectedKey.length) {
      message.warning('请选择要提交的数据！');
    } else {
      this.submit();
    }
  };
  selectChange = key => {
    this.setState({ selectedKey: key });
  };
  render() {
    let {
      searchForm,
      columns,
      loading,
      selectedKey,
      data,
      pagination,
      visibel,
      model,
    } = this.state;
    const { getFieldDecorator, id } = this.props.form;
    const rowSelection = {
      onChange: this.selectChange,
      selectedRowKeys: selectedKey,
    };
    return (
      <div>
        <SearchArea
          searchForm={searchForm}
          submitHandle={this.handleSearch}
          clearHandle={this.reset}
          maxLength={4}
        />

        <Button style={{ margin: '10px 20px 10px 0' }} onClick={this.submitClick} type="primary">
          提交
        </Button>
        <Table
          onClick={this.handleRowClick}
          dataSource={data}
          pagination={pagination}
          loading={loading}
          bordered
          columns={columns}
          onRowClick={this.handleRowClick}
          scroll={{ x: 1500 }}
          rowSelection={rowSelection}
        />
        <SlideFrame
          show={visibel}
          onClose={() => {
            this.setState({
              visibel: false,
              model: {},
            });
          }}
        >
          <NewDetailsData params={model} close={this.close} />
        </SlideFrame>
      </div>
    );
  }
}

function mapStateToProps(state) {
  return {
    company: state.user.company,
    user: state.user.currentUser,
  };
}

export default connect(mapStateToProps)(Form.create()(TransactioDetailsDataSupplement));
