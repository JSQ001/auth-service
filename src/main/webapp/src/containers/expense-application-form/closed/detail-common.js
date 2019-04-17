import React from 'react';
import { connect } from 'dva';
import { Form, Button, Row, Col, Breadcrumb, message, Modal, Card, Input } from 'antd';
import Table from 'widget/table';
const FormItem = Form.Item;
const { TextArea } = Input;
import DocumentBasicInfo from 'widget/Template/document-basic-info';
import moment from 'moment';
import PropTypes from 'prop-types';
import ApproveHistory from 'widget/Template/approve-history-work-flow';
import service from 'containers/expense-application-form/service';
import 'styles/pre-payment/my-pre-payment/pre-payment-detail.scss';
import { messages } from 'utils/utils';
import { routerRedux } from 'dva/router';

class ApplicationCommon extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      lineLoading: false,
      historyLoading: false, //控制审批历史记录是否loading
      columns: [
        {
          title: '序号',
          dataIndex: 'number',
          width: 90,
          render: (value, record, index) =>
            (this.state.pagination.current - 1) * this.state.pagination.pageSize + index + 1,
        },
        {
          title: '申请类型',
          dataIndex: 'expenseTypeName',
          width: 150,
        },
        {
          title: '申请金额',
          dataIndex: 'amount',
          width: 120,
          render: value => this.filterMoney(value),
        },
        {
          title: '本位币金额',
          dataIndex: 'functionalAmount',
          width: 120,
          render: value => this.filterMoney(value),
        },
        {
          title: '可关闭金额',
          dataIndex: 'canCloseAmount',
          width: 120,
          render: value => this.filterMoney(value),
        },
      ],
      record: {},
      approveHistory: [],
      headerInfo: {},
      lineInfo: {},
      modalVisible: false,
      btLoading: false,
      closedRecord: {},
      isCloseHeader: false,
      pagination: {
        current: 1,
        showSizeChanger: true,
        pageSize: 5,
        pageSizeOptions: ['5', '10', '20', '50', '100'],
        showTotal: (total, range) =>
          this.$t('common.show.total', {
            range0: `${range[0]}`,
            range1: `${range[1]}`,
            total: total,
          }),
      },
    };
  }

  componentDidMount() {
    //设置基本信息
    this.setBasicInfo();
    //设置表格动态列
    this.setTableColumns();
  }

  //设置基本信息
  setBasicInfo = () => {
    const { headerData } = this.props;
    let headerInfo = {
      businessCode: headerData.documentNumber,
      createdDate: headerData.requisitionDate,
      formName: headerData.typeName,
      createByName: headerData.createdName,
      currencyCode: headerData.currencyCode,
      totalAmount: headerData.totalFunctionAmount,
      statusCode: headerData.status,
      remark: headerData.remarks,
      infoList: [
        { label: '申请人', value: headerData.employeeName },
        { label: '公司', value: headerData.companyName },
        { label: '部门', value: headerData.departmentName },
      ],
      customList: headerData.dimensions
        ? headerData.dimensions
            .filter(o => o.headerFlag)
            .map(o => ({ label: o.name, value: o.valueName }))
        : [],
      attachments: headerData.attachments,
    };

    if (headerData.associateContract) {
      headerInfo.infoList.push({
        label: '关联合同',
        value: headerData.contractNumber,
        linkId: headerData.contractHeaderId,
      });
    }
    this.setState({ headerInfo });
  };

  //设置表格动态列
  setTableColumns = () => {
    const { headerData } = this.props;
    let { columns } = this.state;

    service.getColumnInfo(headerData.id).then(res => {
      res.data.map(item => {
        columns.push({
          ...item,
          width: 150,
        });
      });

      let option = {
        title: '备注',
        dataIndex: 'remarks',
        align: 'center',
      };
      if (res.data.length > 4) {
        option.width = 350;
      }
      columns.push(option);

      //关闭
      columns.push({
        title: '操作',
        dataIndex: 'options',
        width: 120,
        align: 'center',
        fixed: 'right',
        render: (value, record) =>
          record.closedFlag !== 'CLOSED' && <a onClick={() => this.closeLine(record)}>关闭</a>,
      });

      this.setState({ columns }, () => {
        this.getLineInfo();
      });
    });
  };

  //获取行数据
  getLineInfo = () => {
    const { headerData } = this.props;
    const {
      pagination: { pageSize, current },
      pagination,
    } = this.state;
    this.setState({ lineLoading: true });
    service
      .getCloseApplicationLines(headerData.id, { size: pageSize, page: current - 1 })
      .then(res => {
        let { headerInfo } = this.state;
        headerInfo.totalAmount = res.data.currencyAmount ? res.data.currencyAmount.amount : '0.00';
        this.setState({
          headerInfo,
          lineInfo: res.data,
          lineLoading: false,
          pagination: { ...pagination, total: Number(res.headers['x-total-count']) },
        });
      })
      .catch(err => {
        message.error(err.response.data.message);
      });
  };

  //关闭行
  closeLine = record => {
    this.setState({ closedRecord: record, modalVisible: true, isCloseHeader: false });
  };

  // 确认关闭按钮
  handleClose = () => {
    this.props.form.validateFieldsAndScroll((err, values) => {
      if (!err) {
        if (this.state.isCloseHeader) {
          this.setState({ btLoading: true });
          let headerIds = [];
          headerIds.push(this.props.headerData.id);
          let params = {
            headerIds: headerIds,
            messages: values.messages,
          };
          service
            .closedFunction(params)
            .then(res => {
              if (res.status === 200) {
                message.success(messages('common.operate.success') /**操作成功 */);
                this.setState({
                  btLoading: false,
                  modalVisible: false,
                });
                this.backHandle();
              }
            })
            .catch(e => {
              this.setState({ btLoading: false });
              if (e.response) {
                message.error(
                  messages('common.operate.filed' /*保存失败*/) + '!' + e.response.data.message
                );
              } else {
                message.error(messages('common.operate.filed'));
              }
            });
        } else {
          this.setState({ btLoading: true });
          let record = this.state.closedRecord;
          service
            .closeLine(record.id, record.headerId, values.messages)
            .then(res => {
              if (res.status === 200) {
                message.success(messages('common.operate.success') /**操作成功 */);
                let { pagination } = this.state;
                pagination.current = 1;
                this.setState(
                  {
                    pagination,
                    btLoading: false,
                    modalVisible: false,
                  },
                  () => {
                    this.getLineInfo();
                  }
                );
              }
            })
            .catch(e => {
              this.setState({ btLoading: false });
              if (e.response) {
                message.error(
                  messages('common.operate.filed' /*保存失败*/) + '!' + e.response.data.message
                );
              } else {
                message.error(messages('common.operate.filed'));
              }
            });
        }
      }
    });
  };

  //取消
  backHandle = () => {
    this.props.dispatch(
      routerRedux.push({
        pathname: '/expense-application/expense-application-closed/expense-application-closed',
      })
    );
  };

  //扩展行
  expandedRow = record => {
    if (record.currencyCode == this.props.company.baseCurrency) return null;
    return (
      <div>
        <Row>
          <Col span={2}>
            <span style={{ float: 'right' }}>金额属性</span>
          </Col>
          <Col span={6} offset={1}>
            汇率日期：{moment(record.exchangeDate).format('YYYY-MM-DD')}
          </Col>
          <Col span={6}>汇率：{record.exchangeRate}</Col>
        </Row>
      </div>
    );
  };

  tableChange = pagination => {
    this.setState({ pagination }, () => {
      this.getLineInfo();
    });
  };

  render() {
    const { lineInfo, columns, lineLoading, pagination, headerInfo, btLoading } = this.state;
    const { headerData } = this.props;
    const formItemLayout = {
      labelCol: { span: 6 },
      wrapperCol: { span: 14, offset: 1 },
    };
    const { getFieldDecorator } = this.props.form;
    /**根据单据状态确定该显示什么按钮 */
    let status = (
      <h3 className="header-title" style={{ textAlign: 'right', marginBottom: '10px' }}>
        <Button
          style={{ color: '#fff', background: '#f04134' }}
          onClick={() => this.setState({ modalVisible: true, isCloseHeader: true })}
        >
          关闭
        </Button>
      </h3>
    );
    return (
      <div className="pre-payment-common">
        <Card
          style={{
            boxShadow: '0 2px 8px rgba(0, 0, 0, 0.15)',
          }}
          bodyStyle={{ padding: '24px 32px', paddingTop: 0 }}
        >
          <DocumentBasicInfo params={headerInfo}>{status}</DocumentBasicInfo>
        </Card>

        <Card
          style={{
            marginTop: 20,
            boxShadow: '0 2px 8px rgba(0, 0, 0, 0.15)',
          }}
          title="申请信息"
        >
          <div className="table-header">
            {lineInfo.currencyAmount && (
              <div style={{ float: 'right' }}>
                <Breadcrumb style={{ marginBottom: '10px', lineHeight: '32px' }}>
                  <Breadcrumb.Item>
                    申请金额:
                    <span style={{ color: 'green' }}>
                      {' ' + lineInfo.currencyAmount.currencyCode}{' '}
                      {this.filterMoney(lineInfo.currencyAmount.amount)}
                    </span>
                  </Breadcrumb.Item>
                  <Breadcrumb.Item>
                    本币金额:<span style={{ color: 'green' }}>
                      {' ' + this.props.company.baseCurrency}{' '}
                      {this.filterMoney(lineInfo.currencyAmount.functionalAmount)}
                    </span>
                  </Breadcrumb.Item>
                </Breadcrumb>
              </div>
            )}
          </div>
          <Table
            style={{ clear: 'both' }}
            rowKey={record => record.id}
            columns={columns}
            dataSource={lineInfo.lines || []}
            bordered
            loading={lineLoading}
            size="middle"
            pagination={pagination}
            expandedRowRender={this.expandedRow}
            onChange={this.tableChange}
            scroll={{ x: 1300 }}
          />
        </Card>
        {/** 关闭按钮的弹出框 */}
        <Modal
          title="关闭申请单"
          visible={this.state.modalVisible}
          onOk={this.handleClose}
          confirmLoading={btLoading}
          destroyOnClose={true}
          onCancel={() => this.setState({ modalVisible: false, isCloseHeader: false })}
          okText="确认"
          cancelText="取消"
          cancelButtonProps={{ loading: btLoading }}
        >
          <Form>
            <FormItem {...formItemLayout} label="关闭原因">
              {getFieldDecorator('messages', {
                rules: [
                  {
                    required: true,
                    message: '请输入关闭原因',
                  },
                ],
              })(
                <TextArea
                  autosize={{ minRows: 2 }}
                  style={{ minWidth: '100%' }}
                  placeholder={this.$t({ id: 'common.please.enter' } /*请输入*/)}
                />
              )}
            </FormItem>
          </Form>
        </Modal>
        <div
          style={{
            marginTop: 20,
            boxShadow: '0 2px 8px rgba(0, 0, 0, 0.15)',
          }}
        >
          <ApproveHistory
            type="801009"
            oid={headerData.documentOid}
            headerId={headerData.id}
            formOid={headerData.formOid}
          />
        </div>
      </div>
    );
  }
}

ApplicationCommon.propTypes = {
  id: PropTypes.any.isRequired, //显示数据
  flag: PropTypes.bool, //是否显示审批历史
};

ApplicationCommon.defaultProps = {
  flag: true,
};

const wrappedApplicationCommon = Form.create()(ApplicationCommon);
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
)(wrappedApplicationCommon);
