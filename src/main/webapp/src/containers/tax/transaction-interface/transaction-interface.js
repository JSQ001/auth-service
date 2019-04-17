import React, { Component } from 'react';
import { connect } from 'dva';
import SearchArea from 'widget/search-area';
import { Button, Divider, Popconfirm, Form, Modal, Select, Row } from 'antd';
import config from 'config';
import CustomTable from 'widget/custom-table';
import SlideFrame from 'widget/slide-frame';
import TransactionInterfaceDetail from './transaction-interface-detail';
const confirm = Modal.confirm;
import jobService from 'containers/job/job.service';
import { routerRedux } from 'dva/router';
class TaxVatTranInterface extends Component {
  constructor(props) {
    super(props);
    this.state = {
      ruleParameterTypeArray: [], //值列表
      searchForm: [
        {
          type: 'value_list',
          id: 'sourceSystem',
          placeholder: '请输入',
          label: '来源系统',
          colSpan: 6,
          valueListCode: 'TRAN_IMPORT_SYS', //需要提醒业务在系统定义
          options: [],
          colSpan: 6,
        },
        {
          type: 'select',
          label: '机构',
          id: 'org',
          getUrl: `${config.mdataUrl}/api/company/by/condition`,
          getParams: { setOfBooksId: props.company.setOfBooksId },
          method: 'get',
          options: [],
          colSpan: '6',
          valueKey: 'id',
          labelKey: 'name',
        },
        {
          type: 'input',
          id: 'clientAcc',
          placeholder: '请输入',
          label: '客户编号',
          colSpan: 6,
        },
        {
          type: 'input',
          id: 'matchField1',
          placeholder: '请输入',
          label: '业务类型',
          colSpan: 6,
        },
        {
          type: 'input',
          id: 'tranNum',
          placeholder: '请输入',
          label: '交易流水号',
          colSpan: 6,
        },
        {
          type: 'items',
          id: 'date',
          items: [
            { type: 'date', id: 'tranDateFrom', label: '交易日期从' },
            { type: 'date', id: 'tranDateTo', label: '交易日期至' },
          ],
          colSpan: 6,
        },
      ],
      columns: [
        {
          title: '交易流水号',
          dataIndex: 'tranNum',
          align: 'center',
        },
        {
          title: '业务类型',
          dataIndex: 'matchField1',
          align: 'center',
        },
        {
          title: '业务小类',
          dataIndex: 'matchField2',
          align: 'center',
        },
        {
          title: '客户编号',
          dataIndex: 'clientAcc',
          align: 'center',
        },
        {
          title: '客户纳税人名称',
          dataIndex: 'taxpayerName',
          align: 'center',
        },
        {
          title: '交易日期',
          dataIndex: 'tranDate',
          align: 'center',
        },
        {
          title: '机构',
          dataIndex: 'org',
          align: 'center',
        },
        {
          title: '责任中心',
          dataIndex: 'costCenter',
          align: 'center',
        },
        {
          title: '本币金额',
          dataIndex: 'functionalAmount',
          align: 'center',
        },
        {
          title: '本币销售额',
          dataIndex: 'funSales',
          align: 'center',
        },

        {
          title: '本币销项税额',
          dataIndex: 'funOutTaxes',
          align: 'center',
        },
        {
          title: '来源系统',
          dataIndex: 'sourceSystem',
          align: 'center',
        },
        {
          title: '数据产生日期',
          dataIndex: 'sysDate',
          align: 'center',
        },
        {
          title: '创建日期',
          dataIndex: 'creationDate',
          align: 'center',
        },
      ],
      searchParams: {},
      visibel: false,
      model: {},
      slideFrameParams: {},
      lsVisible: false,
    };
  }

  //搜索
  search = values => {
    this.setState({ searchParams: values, page: 0 }, () => {
      this.getList();
    });
  };

  //获取列表
  getList = () => {
    let { searchParams } = this.state;
    this.table.search(searchParams);
  };

  // 清除搜索条件
  empty = () => {
    this.search({});
  };
  //关闭侧拉框
  close = () => {
    this.setState({ visibel: false });
  };
  //跳转到详情
  handleRowClick = record => {
    this.setState({
      visibel: true,
      slideFrameParams: { period: record, hasInit: false },
    });
  };
  handleAfterClose = params => {
    this.setState(
      {
        visibel: false,
      },
      () => {
        params && this.getList();
      }
    );
  };
  //上一页
  lastPage = params => {
    console.log(params);
  };

  //下一页
  nextPage = params => {
    console.log(params);
  };
  //获取数据
  getdata = () => {
    this.setState({
      lsVisible: true,
    });
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
  handleSubmit = preps => {
    this.props.form.validateFields((err, values) => {
      this.run(values.importSysId);
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
  showConfirm() {
    console.log(this.state);
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
  onCancel = () => {
    this.setState({
      ruleParameterTypeArray: [],
      lsVisible: false,
    });
  };
  render() {
    const {
      searchForm,
      columns,
      data,
      loading,
      visibel,
      lsVisible,
      pagination,
      model,
      slideFrameParams,
      ruleParameterTypeArray,
    } = this.state;
    const { getFieldDecorator } = this.props.form;
    return (
      <div>
        <SearchArea searchForm={searchForm} submitHandle={this.search} clearHandle={this.empty} />
        <Button
          style={{ margin: '20px 0' }}
          className="create-btn"
          type="primary"
          onClick={this.getdata}
        >
          获取数据
        </Button>
        <CustomTable
          onClick={this.handleRowClick}
          columns={columns}
          url={`${config.baseUrl}/tax/api/tax/vat/tran/interface/pageByCondition`}
          ref={ref => (this.table = ref)}
          //onRowClick={this.handleRowClick}
        />
        <SlideFrame title="交易数据接口明细" onClose={this.close} show={visibel} width={'45vw'}>
          <TransactionInterfaceDetail
            params={{ ...slideFrameParams, visible: visibel }}
            onClose={this.handleAfterClose}
            lastPage={this.lastPage}
            nextPage={this.nextPage}
          />
        </SlideFrame>

        <Modal
          title={this.$t({ id: 'tax.git.customer.interface' })}
          visible={lsVisible}
          centered={true}
          width={600}
          closable={false}
          onCancel={this.onClose}
          footer={[]}
        >
          <Form onSubmit={this.handleSubmit}>
            <Form.Item label="来源系统">
              {getFieldDecorator('importSysId', {
                rules: [
                  {
                    required: false,
                  },
                ],
              })(
                <Select
                  className="input-disabled-color"
                  placeholder={this.$t({ id: 'common.please.select' })}
                  onChange={this.onSelect}
                  onFocus={() => this.getValueList('TRAN_IMPORT_SYS', ruleParameterTypeArray)}
                >
                  {ruleParameterTypeArray.map(item => (
                    <Select.Option key={item.id}>{item.value}</Select.Option>
                  ))}
                </Select>
              )}
            </Form.Item>
            <Row style={{ textAlign: 'right' }}>
              <Button type="primary" htmlType="submit" loading={loading}>
                {this.$t({ id: 'common.submit' })}
              </Button>
              <Button style={{ marginLeft: 8 }} onClick={this.onCancel}>
                {this.$t({ id: 'common.cancel' })}
              </Button>
            </Row>
          </Form>
        </Modal>
      </div>
    );
  }
}

const wrappedTaxVatTranInterface = Form.create()(TaxVatTranInterface);

function mapStateToProps(state) {
  return {
    user: state.user.currentUser,
    company: state.user.company,
    languages: state.languages,
  };
}

export default connect(
  mapStateToProps,
  null,
  null,
  { withRef: true }
)(wrappedTaxVatTranInterface);
