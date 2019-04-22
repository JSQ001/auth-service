import React, { Component } from 'react';
import { Form, Select, message, Input, Row, Col, Table, Popover, Button } from 'antd';
import service from './service';

const FormItem = Form.Item;

class EditLineFrame extends Component {
  constructor(props) {
    super(props);
    this.state = {
      useTypeList: [],
      columns: [
        {
          title: this.$t('expense-report.association.request'),
          dataIndex: 'applicationNumber',
          align: 'center',
          render: (value, record) => {
            return (
              <Popover content={value}>
                <a onClick={() => this.showDocumentDetail(record)}>{value}</a>
              </Popover>
            );
          },
          width: 120,
        },
        {
          title: this.$t('acp.company'),
          dataIndex: 'companyName',
          align: 'center',
          render: value => {
            return (
              <Popover content={value}>
                <span>{value}</span>
              </Popover>
            );
          },
          width: 120,
        },
        {
          title: this.$t('common.department'),
          dataIndex: 'departmentName',
          align: 'center',
          render: value => {
            return (
              <Popover content={value}>
                <span>{value}</span>
              </Popover>
            );
          },
          width: 120,
        },
        {
          title: this.$t('expense.apportion.amount'),
          dataIndex: 'distAmount',
          align: 'center',
          render: amount => {
            return <Popover content={amount}>{this.filterMoney(amount, 2)}</Popover>;
          },
          width: 120,
        },
        {
          title: this.$t('tax.share.tax'),
          dataIndex: 'distTaxAmount',
          align: 'center',
          render: amount => {
            return <Popover content={amount}>{this.filterMoney(amount, 2)}</Popover>;
          },
          width: 120,
        },
      ],
      selectedRowKeys: [], // 选中的行rowKey
      pagination: {
        showSizeChanger: true,
        showQuickJumper: true,
        showTotal: (total, range) =>
          this.$t('common.show.total', {
            range0: `${range[0]}`,
            range1: `${range[1]}`,
            total,
          }),
      },
      saveLoading: false,
    };
  }

  componentDidMount = () => {
    // 用途类型
    this.getSystemValueList('useType')
      .then(res => {
        const useTypeList = res.data.values;
        this.setState({ useTypeList });
      })
      .catch(err => {
        message.error(err.response.data.message);
      });

    const {
      params: { expInputForReportDistDTOS },
    } = this.props;

    const selectedRowKeys = expInputForReportDistDTOS
      .filter(o => o.selectFlag === 'Y')
      .map(o => o.expReportDistId);

    this.setState({ selectedRowKeys });
  };

  // 多选
  handleGetShareTax = (selectedRowKeys, selectedRows) => {
    this.setState({
      selectedRowKeys,
    });
    console.log(selectedRows);
  };

  // 关联申请单
  showDocumentDetail = () => {};

  // 保存
  handleSubmit = e => {
    e.preventDefault();
    const { selectedRowKeys } = this.state;
    const { form, params } = this.props;
    const { validateFields } = form;

    validateFields((err, values) => {
      if (err) return;
      this.setState({ saveLoading: true });

      if (selectedRowKeys.length === params.expInputForReportDistDTOS.length) {
        params.selectFlag = 'Y';
      } else if (selectedRowKeys.length > 0) {
        params.selectFlag = 'P';
      } else {
        params.selectFlag = 'N';
      }
      selectedRowKeys.forEach(value => {
        const row = params.expInputForReportDistDTOS.find(o => o.expReportDistId === value);
        if (row) {
          row.selectFlag = 'Y';
        }
      });
      service
        .saveExpenseLine([{ ...params, ...values, useTypeName: '' }])
        .then(res => {
          if (res) {
            message.success(this.$t('common.update.success'));
            this.setState({ saveLoading: false }, () => {
              const { onClose } = this.props;
              onClose(true);
            });
          }
        })
        .catch(error => {
          message.error(error.response.data.message);
        });
    });
  };

  // 取消
  handleCancel = () => {
    const { onClose } = this.props;
    onClose();
  };

  // 分页
  tablePageChange = pagination => {
    const { page, size } = this.state;
    this.setState({
      page: pagination.current - 1 || page,
      size: pagination.pageSize || size,
    });
  };

  render() {
    const { form, params, type } = this.props;
    const { getFieldDecorator } = form;
    const formItemLayout = {
      labelCol: { span: 8 },
      wrapperCol: { span: 10 },
    };
    const { useTypeList, columns, selectedRowKeys, pagination, saveLoading } = this.state;
    const rowSelection = {
      selectedRowKeys,
      onChange: this.handleGetShareTax,
    };
    return (
      <div>
        <Form>
          <FormItem {...formItemLayout} label={this.$t('tax.business.categories')}>
            {getFieldDecorator('transferTypeName', {
              rules: [
                {
                  required: true,
                  message: this.$t({ id: 'common.please.select' }),
                },
              ],
              initialValue: params.transferTypeName,
            })(<Input disabled />)}
          </FormItem>
          <FormItem {...formItemLayout} label={this.$t('转出比例')}>
            {getFieldDecorator('transferProportion', {
              rules: [
                {
                  required: true,
                  message: this.$t({ id: 'common.please.enter' }),
                },
              ],
              initialValue: params.transferProportion,
            })(<Input placeholder={this.$t('common.please.enter')} disabled />)}
          </FormItem>
          <FormItem {...formItemLayout} label={this.$t('tax.use.type')}>
            {getFieldDecorator('useType', {
              rules: [
                {
                  required: true,
                  message: this.$t({ id: 'common.please.enter' }),
                },
              ],
              initialValue: params.useType,
            })(
              <Select
                placeholder={this.$t('common.please.select')}
                getPopupContainer={triggerNode => triggerNode.parentNode}
              >
                {useTypeList.map(item => {
                  return <Select.Option key={item.value}>{item.name}</Select.Option>;
                })}
              </Select>
            )}
          </FormItem>
          <FormItem {...formItemLayout} label={this.$t('common.expense.type')}>
            {getFieldDecorator('expenseTypeName', {
              rules: [
                {
                  required: true,
                  message: this.$t({ id: 'common.please.enter' }),
                },
              ],
              initialValue: params.expenseTypeName,
            })(
              <Select
                placeholder={this.$t('common.please.select')}
                disabled
                getPopupContainer={triggerNode => triggerNode.parentNode}
              >
                {useTypeList.map(item => {
                  return <Select.Option key={item.value}>{item.name}</Select.Option>;
                })}
              </Select>
            )}
          </FormItem>
          <FormItem
            {...formItemLayout}
            label={
              type === 'FOR_SALE' ? this.$t('tax.sale.amount') : this.$t('tax.turnOut.tax')
              // '视同销售金额' : '转出税额'
            }
          >
            <Row gutter={8}>
              <Col span={10}>
                {getFieldDecorator('currencyCode', {
                  rules: [
                    {
                      required: true,
                      message: this.$t({ id: 'common.please.enter' }),
                    },
                  ],
                  initialValue: params.currencyCode,
                })(<Input disabled />)}
              </Col>
              <Col span={14}>
                {getFieldDecorator('baseAmount', {
                  rules: [
                    {
                      required: true,
                      message: this.$t({ id: 'common.please.enter' }),
                    },
                  ],
                  initialValue: params.baseAmount ? Number(params.baseAmount).toFixed(2) : 0,
                })(<Input disabled />)}
              </Col>
            </Row>
          </FormItem>
          <FormItem
            {...formItemLayout}
            label={
              type === 'FOR_SALE' ? this.$t('tax.as.sale.tax') : this.$t('tax.transfer.amount')
              // '视同销售税额' : '转出金额'
            }
          >
            <Row gutter={8}>
              <Col span={10}>
                {getFieldDecorator('currencyCode', {
                  rules: [
                    {
                      required: true,
                      message: this.$t({ id: 'common.please.enter' }),
                    },
                  ],
                  initialValue: params.currencyCode,
                })(<Input disabled />)}
              </Col>
              <Col span={14}>
                {getFieldDecorator('amount', {
                  rules: [
                    {
                      required: true,
                      message: this.$t({ id: 'common.please.enter' }),
                    },
                  ],
                  initialValue: params.amount ? Number(params.amount).toFixed(2) : 0,
                })(<Input disabled />)}
              </Col>
            </Row>
          </FormItem>
          <FormItem {...formItemLayout} label={this.$t('备注')}>
            {getFieldDecorator('description', {
              initialValue: params.description,
            })(
              <Input.TextArea
                placeholder={this.$t('tax.msg.leave.please')}
                autosize={{ minRows: 3 }}
              />
            )}
          </FormItem>
        </Form>
        <div className="common-item-title">{this.$t('tax.share.info')}</div>
        <Table
          bordered
          columns={columns}
          dataSource={params.expInputForReportDistDTOS}
          style={{ margin: '10px 0 30px 0' }}
          scroll={{ x: 600 }}
          rowSelection={rowSelection}
          rowKey={record => record.expReportDistId}
          pagination={pagination}
          onChange={this.tablePageChange}
        />
        <div className="slide-footer">
          <Button
            type="primary"
            htmlType="submit"
            loading={saveLoading}
            onClick={this.handleSubmit}
          >
            {this.$t('common.save')}
          </Button>
          <Button onClick={this.handleCancel}>{this.$t('common.cancel')}</Button>
        </div>
      </div>
    );
  }
}

export default Form.create()(EditLineFrame);
