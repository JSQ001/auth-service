import React, { Component } from 'react';
import { Row, Col, Form, Modal, Button, message, Spin, Popover } from 'antd';
import Table from 'widget/table';
import moment from 'moment';
import 'styles/reimburse/invoice.scss';
import service from './service';

class InvoiceInfo extends Component {
  constructor(props) {
    super(props);
    this.state = {
      columns: [
        { title: '发票行号', dataIndex: 'invoiceLineNum', width: '45px', align: 'center' },
        {
          title: '货物或应税劳务、服务名称',
          dataIndex: 'goodsName',
          render: goodsName => goodsName || '-',
          align: 'center',
          width: '120px',
        },
        {
          title: '规格型号',
          dataIndex: 'specificationModel',
          render: specificationModel => specificationModel || '-',
          align: 'center',
        },
        { title: '单位', dataIndex: 'unit', align: 'center' },
        { title: '数量', dataIndex: 'num', align: 'center' },
        { title: '单价', dataIndex: 'unitPrice', align: 'center' },
        { title: '金额', dataIndex: 'detailAmount', align: 'center' },
        { title: '税率', dataIndex: 'taxRate', align: 'center' },
        { title: '税额', dataIndex: 'taxAmount', align: 'center' },
      ],
      invoice: { invoiceHead: {}, invoiceLineList: [] },
      spinning: true,
      brief: true,
    };
  }

  componentWillReceiveProps(nextProps) {
    if (nextProps.visible && nextProps.id) {
      this.setState({ spinning: true, brief: true });
      service
        .getInvoiceInfo(nextProps.id)
        .then(res => {
          this.setState({ invoice: res.data, spinning: false });
        })
        .catch(err => message.error(err.response.data.message));
    }
  }

  handleCancel = () => {
    this.props.cancel && this.props.cancel();
  };

  renderDetail = (title, value, span = 12) => {
    let titleSpan = 8;
    return (
      <Col span={span} className="invoice-row">
        <Row>
          <Col span={titleSpan} className="invoice-title">
            {title}:
          </Col>
          <Col span={24 - titleSpan} className="invoice-detail">
            {value || '-'}
          </Col>
        </Row>
      </Col>
    );
  };

  render() {
    const { columns, invoice, spinning } = this.state;
    const { invoiceHead, invoiceLineList } = invoice;
    const validate = this.props.validate;
    return (
      <Modal
        title="发票信息"
        visible={this.props.visible}
        onCancel={this.handleCancel}
        footer={false}
        width="60%"
        bodyStyle={{ padding: 10 }}
      >
        <Spin spinning={spinning} wrapperClassName="invoice">
          {this.state.brief && !validate ? (
            <div className="invoice-brief-mode invoice-other">
              <Row>
                <Col span={6} className="invoice-brief-amount-area">
                  <a onClick={() => this.setState({ brief: false })}>查看详情</a>
                  <Row className="invoice-brief-amount-detail">
                    <Col span={14}>金额合计</Col>
                    <Col span={10}>
                      {invoiceHead.invoiceAmount && invoiceHead.invoiceAmount.toFixed(2)}
                    </Col>
                    <Col span={14}>币种</Col>
                    <Col span={10}>{invoiceHead.currencyCode || 'CNY'}</Col>
                    <Col span={14}>税额合计</Col>
                    <Col span={10}>
                      {invoiceHead.taxTotalAmount && invoiceHead.taxTotalAmount.toFixed(2)}
                    </Col>
                    <Col span={14}>价税合计</Col>
                    <Col span={10}>
                      {invoiceHead.totalAmount && invoiceHead.totalAmount.toFixed(2)}
                    </Col>
                  </Row>
                </Col>
                <Col span={18} className="invoice-brief-content-area">
                  <div className="invoice-type">
                    {invoiceHead.invoiceTypeName}
                    <Button icon="printer" type="primary" onClick={this.handlePrint}>
                      打印
                    </Button>
                  </div>
                  <Row className="invoice-brief-content" gutter={10}>
                    {
                      <Col span={12}>
                        <span className="title">开票日期：</span>&nbsp;
                        {moment(invoiceHead.invoiceDate).format('YYYY-MM-DD')}
                      </Col>
                    }
                    <Col span={12}>
                      <span className="title">录入方式：</span>&nbsp;
                      <Popover
                        content={invoiceHead.createdMethodName}
                        overlayStyle={{ width: 200 }}
                      >
                        {invoiceHead.createdMethodName}
                      </Popover>
                    </Col>
                    <Col span={12}>
                      <span className="title">发票代码：</span>&nbsp;
                      {invoiceHead.invoiceCode}
                    </Col>
                    <Col span={12}>
                      <span className="title">发票号码：</span>&nbsp;
                      {invoiceHead.invoiceNo}
                    </Col>
                    <Col span={12}>
                      <span className="title">销售方：</span>&nbsp;
                      <Popover content={invoiceHead.salerName} overlayStyle={{ width: 200 }}>
                        {invoiceHead.salerName}
                      </Popover>
                    </Col>
                    <Col span={12}>
                      <span className="title">购买方：</span>&nbsp;
                      <Popover content={invoiceHead.buyerName} overlayStyle={{ width: 200 }}>
                        {invoiceHead.buyerName}
                      </Popover>
                    </Col>
                  </Row>
                </Col>
              </Row>
            </div>
          ) : (
            <>
              <div className="invoice-type">
                {invoiceHead.invoiceTypeName}
                <span style={{ fontSize: '14px', paddingLeft: '10px' }}>
                  (录入方式：{invoiceHead.createdMethodName})
                </span>
                {validate && !invoiceHead.checkResult ? (
                  <div className="no-validate">未验真！</div>
                ) : (
                  <Button
                    icon="printer"
                    type="primary"
                    onClick={this.handlePrint}
                    style={{ float: 'right', marginTop: '9px' }}
                  >
                    打印
                  </Button>
                )}
              </div>
              <div className="invoice-detail-area invoice-t">
                <Row>
                  {this.renderDetail(
                    '开票日期',
                    moment(invoiceHead.invoiceDate).format('YYYY-MM-DD')
                  )}
                  {this.renderDetail('发票代码', invoiceHead.invoiceCode)}
                  {this.renderDetail('发票号码', invoiceHead.invoiceNo)}
                  {this.renderDetail('设备编号', invoiceHead.machineNo)}
                  {this.renderDetail('校验码', invoiceHead.checkCode)}
                </Row>
                <Row className="invoice-row">
                  <Col span={4} className="invoice-title">
                    价税合计:
                  </Col>
                  <Col span={20} className="invoice-detail">
                    <b className="invoice-currency">{invoiceHead.currencyCode || 'CNY'}</b>
                    <b className="invoice-amount">
                      {invoiceHead.totalAmount && invoiceHead.totalAmount.toFixed(2)}
                    </b>
                    (金额合计：{invoiceHead.invoiceAmount && invoiceHead.invoiceAmount.toFixed(2)}&nbsp;&nbsp;
                    税额合计：{invoiceHead.taxTotalAmount && invoiceHead.taxTotalAmount.toFixed(2)})
                  </Col>
                </Row>
                <Row>
                  <Col span={4} className="invoice-title">
                    备注:
                  </Col>
                  <Col span={20} className="invoice-detail">
                    {invoiceHead.remark || '-'}
                  </Col>
                </Row>
                <Table
                  size="small"
                  style={{ margin: '10px 0' }}
                  columns={columns}
                  dataSource={invoiceLineList}
                  rowKey={record => record.id}
                  pagination={false}
                />
                <Row>
                  {this.renderDetail('购买方', invoiceHead.buyerName, 24)}
                  {this.renderDetail('纳税人识别号', invoiceHead.buyerTaxNo, 24)}
                  {this.renderDetail('地址/电话', invoiceHead.buyerAddPh, 24)}
                  {this.renderDetail('开户行/账号', invoiceHead.buyerAccount, 24)}
                </Row>
                <hr style={{ borderColor: '#e8e8e8' }} />
                <Row>
                  {this.renderDetail('销售方', invoiceHead.salerName, 24)}
                  {this.renderDetail('纳税人识别号', invoiceHead.salerTaxNo, 24)}
                  {this.renderDetail('地址/电话', invoiceHead.salerAddPh, 24)}
                  {this.renderDetail('开户行/账号', invoiceHead.salerAccount, 24)}
                </Row>
              </div>
              {!validate && (
                <div className="invoice-operate">
                  <a onClick={() => this.setState({ brief: true })}>收起详情</a>
                </div>
              )}
            </>
          )}
        </Spin>
      </Modal>
    );
  }
}

export default Form.create()(InvoiceInfo);
