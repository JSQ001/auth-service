import React, { Component } from 'react';
import SearchArea from 'widget/search-area';
import { Button, Divider, message, Popconfirm, Modal, Form, Select, Row } from 'antd';
import SlideFrame from 'widget/slide-frame';
import config from 'config';
import moment from 'moment';
import { connect } from 'dva';
import CustomTable from 'widget/custom-table';
import { routerRedux } from 'dva/router';
import jobService from 'containers/job/job.service';
const confirm = Modal.confirm;
class CustomerInterface extends Component {
  constructor(props) {
    super(props);
    this.state = {
      ruleParameterTypeArray: [], //值列表
      importSysId: '',
      searchForm: [
        {
          type: 'value_list',
          id: 'importSys',
          label: '来源系统',
          valueListCode: 'IMPORT_SYS', //需要提醒业务在系统定义
          options: [],
          colSpan: 6,
        },
        {
          type: 'input',
          id: 'org',
          label: '归属机构',
          //isRequired: true,
          colSpan: 6,
        },
        {
          type: 'input',
          id: 'clientAcc',
          label: '客户编号',
          colSpan: 6,
        },
        {
          type: 'input',
          id: 'clientName',
          label: '客户名称',
          colSpan: 6,
        },
        {
          type: 'input',
          id: 'taxpayerName',
          label: '纳税人名称',
          colSpan: 6,
        },
        {
          type: 'input',
          id: 'clientTaxNum',
          label: '纳税人识别号',
          colSpan: 6,
        },
        {
          type: 'input',
          id: 'project',
          label: '项目',
          colSpan: 6,
        },
        {
          type: 'items',
          id: 'date',
          items: [
            { type: 'date', id: 'dateFrom', label: '更新日期从' },
            { type: 'date', id: 'dateTo', label: '更新日期至' },
          ],
          colSpan: 6,
        },
      ],
      columns: [
        // {
        //     title: "ID",
        //     dataIndex: "id",
        //     align: "center"
        // },
        {
          title: '客户编号',
          dataIndex: 'clientAcc',
          align: 'center',
        },
        {
          title: '客户名称',
          dataIndex: 'clientTaxName',
          align: 'center',
        },
        {
          title: '客户类型',
          dataIndex: 'clientTaxTypeName',
          align: 'center',
        },
        {
          title: '纳税人名称',
          dataIndex: 'taxpayerName',
          align: 'center',
        },
        {
          title: '纳税人识别号',
          dataIndex: 'clientTaxNum',
          align: 'center',
        },
        {
          title: '地址',
          dataIndex: 'clientTaxAdd',
          align: 'center',
        },
        {
          title: '电话',
          dataIndex: 'clientTaxTel',
          align: 'center',
        },
        {
          title: '银行开户行',
          dataIndex: 'clientTaxBank',
          align: 'center',
        },
        {
          title: '银行账号',
          dataIndex: 'clientTaxAcc',
          align: 'center',
        },
        {
          title: '邮箱',
          dataIndex: 'clientEmail',
          align: 'center',
        },
        {
          title: '手机',
          dataIndex: 'clientPhone',
          align: 'center',
        },
        {
          title: '归属机构',
          dataIndex: 'org',
          align: 'center',
        },
        {
          title: '项目',
          dataIndex: 'project',
          align: 'center',
        },
        {
          title: '来源系统',
          dataIndex: 'importSysName',
          align: 'center',
        },
        {
          title: '更新日期',
          dataIndex: 'createdDate',
          align: 'center',
        },
      ],
      importSys: [
        {
          type: 'value_list',
          id: 'importSys',
          //  label: messages('code.rule.document.type') /*单据类型'*/,
          valueListCode: 2023,
          options: [],
        },
      ],
      searchParams: {},
      visibel: false,
      model: {},
      lsVisible: false,
    };
    this.showConfirm = this.showConfirm.bind(this);
  }
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
  search = values => {
    let params = { ...this.state.searchParams, ...values };

    params.dateFrom && (params.dateFrom = moment(params.dateFrom).format('YYYY-MM-DD'));
    params.dateTo && (params.dateTo = moment(params.dateTo).format('YYYY-MM-DD'));

    this.setState({ searchParams: params }, () => {
      this.table.search(this.state.searchParams);
    });
  };
  //重置
  reset = () => {};

  //获取数据
  getdata = () => {
    this.setState({
      lsVisible: true,
    });
  };

  //跳转到详情
  handleRowClick = record => {
    console.log('record.id=' + record.clientTaxName);
    this.props.dispatch(
      routerRedux.push({
        pathname: '/inter-management/cust-inter/customer-inter-detail/' + record.id,
      })
    );
  };
  onCancel = () => {
    this.setState({
      ruleParameterTypeArray: [],
      lsVisible: false,
    });
  };
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

  render() {
    let { searchForm, columns, lsVisible, ruleParameterTypeArray, loading } = this.state;
    const { getFieldDecorator } = this.props.form;

    return (
      <div>
        <SearchArea searchForm={searchForm} submitHandle={this.search} clearHandle={this.reset} />
        <div style={{ margin: '20px 0' }}>
          <Button type="primary" onClick={this.getdata}>
            获取数据
          </Button>
        </div>
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
                  onFocus={() => this.getValueList('IMPORT_SYS', ruleParameterTypeArray)}
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
        <CustomTable
          onClick={this.handleRowClick}
          columns={columns}
          url={`${config.baseUrl}/tax/api/tax/client/interface/pageByCondition`}
          ref={ref => (this.table = ref)}
          onRowClick={this.handleRowClick}
        />
      </div>
    );
  }
}

//export default CustomerInterface

function mapStateToProps(state) {
  return {
    company: state.user.company,
    user: state.user.currentUser,
  };
}

export default connect(mapStateToProps)(Form.create()(CustomerInterface));
