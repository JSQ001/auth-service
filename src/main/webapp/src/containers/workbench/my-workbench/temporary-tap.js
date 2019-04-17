import React, { Component } from 'react';
import { Button, Row, Col, Modal, Input, message, Form } from 'antd';
import Table from 'widget/table';
import { routerRedux } from 'dva/router';
import { connect } from 'dva';
import Service from './service';

const { Search } = Input;

class TemporaryTap extends Component {
  constructor(props) {
    super(props);
    this.state = {
      detailColumns: [
        {
          title: '单据编号',
          dataIndex: 'documentNumber',
          align: 'center',
        },
        {
          title: '单据公司',
          dataIndex: 'companyName',
          align: 'center',
        },
        {
          title: '业务类别',
          dataIndex: 'businessTypeName',
          align: 'center',
        },
        {
          title: '单据类型',
          dataIndex: 'documentTypeName',
          align: 'center',
        },
        {
          title: '申请人',
          dataIndex: 'employeeName',
          align: 'center',
        },
        {
          title: '入池日期',
          dataIndex: 'enterDate',
          align: 'center',
        },
        {
          title: '当前业务节点',
          dataIndex: 'currentNodeName',
          align: 'center',
        },
        {
          title: '上一业务节点',
          dataIndex: 'lastNodeName',
          align: 'center',
        },
        {
          title: '上一操作人',
          dataIndex: 'lastOperatorName',
          align: 'center',
        },
        {
          title: '上一操作人工作组',
          dataIndex: 'lastWorkTeamName',
          align: 'center',
        },
      ],
      pagination: {
        showSizeChanger: true,
        showQuickJumper: true,
      },
      selectedData: [],
      visible: false,
    };
  }

  componentDidMount() {
    this.search();
  }

  /**
   * 切换分页
   */
  onChangeCheckedPage = page => {
    const { temporaryTapCallBack, temporaryCurrentPage, temporarySize } = this.props;
    if (page - 1 !== temporaryCurrentPage) {
      temporaryTapCallBack(page - 1, temporarySize);
    }
  };

  /**
   * 切换每页显示的条数
   */
  onShowSizeChange = (current, pageSize) => {
    const { temporaryTapCallBack, temporaryCurrentPage } = this.props;
    temporaryTapCallBack(temporaryCurrentPage, pageSize);
  };

  /**
   * 表格已选数据
   */
  onSelectChange = e => {
    this.setState({
      selectedData: e,
    });
  };

  /**
   * 打开取消暂挂弹窗
   */
  cancelHold = () => {
    const { selectedData } = this.state;
    if (selectedData && selectedData.length > 0) {
      this.setState({ visible: true });
    } else {
      message.warning('请选择数据');
    }
  };

  /**
   * 取消暂挂
   */
  handleOk = () => {
    const { selectedData } = this.state;
    const {
      form,
      temporaryTapCallBack,
      temporaryCurrentPage,
      temporarySize,
      isShowLoading,
    } = this.props;
    const formData = form.getFieldsValue();
    const params = {
      ids: selectedData,
      operatorText: (formData && formData.reason) || '',
    };
    isShowLoading(true);
    Service.cancelHold(params)
      .then(() => {
        this.setState(
          {
            visible: false,
            selectedData: [],
          },
          () => {
            isShowLoading(false);
            message.success('取消暂挂成功');
            form.resetFields();
            // 重载表格数据
            temporaryTapCallBack(temporaryCurrentPage, temporarySize);
          }
        );
      })
      .catch(() => {
        message.warn('暂挂失败');
      });
  };

  /**
   * 关闭弹窗
   */
  handleCancel = () => {
    const { form } = this.props;
    form.resetFields();
    this.setState({ visible: false });
  };

  /**
   * 搜索
   */
  search = e => {
    const { setDocumentNumberCB } = this.props;
    setDocumentNumberCB(e);
  };

  /**
   * 点击跳转
   */
  select = record => {
    if (record && record.pageContent) {
      const { dispatch } = this.props;
      dispatch(
        routerRedux.push({
          pathname: `/workbench/my-workbench/detail-reimburse-pending/${record.id}/${
            record.documentId
          }`,
        })
      );
    }
  };

  render() {
    const { detailColumns, pagination, visible, selectedData } = this.state;
    const {
      dataSource,
      temporaryTableTotalNum,
      temporaryCurrentPage,
      temporarySize,
      form,
    } = this.props;
    const rowSelection = {
      selectedRowKeys: selectedData,
      onChange: this.onSelectChange,
    };
    const { getFieldDecorator } = form;

    Object.assign(pagination, {
      total: temporaryTableTotalNum,
      pageSize: temporarySize,
      current: temporaryCurrentPage + 1,
      onChange: this.onChangeCheckedPage,
      onShowSizeChange: this.onShowSizeChange,
      showTotal: total => `共搜到 ${total} 条数据`,
    });

    return (
      <div>
        <Modal title="取消暂挂" visible={visible} onOk={this.handleOk} onCancel={this.handleCancel}>
          <Form layout="inline" onSubmit={this.handleSubmit}>
            <Row style={{ marginBottom: 10, marginTop: 10 }}>
              <Col span={24}>
                <Form.Item labelCol={{ span: 80 }} wrapperCol={{ span: 50 }}>
                  {getFieldDecorator('reason', {})(
                    <Input.TextArea
                      autosize={{ minRows: 2, maxRows: 6 }}
                      placeholder="请输入取消暂挂原因"
                      rows={4}
                    />
                  )}
                </Form.Item>
              </Col>
            </Row>
          </Form>
        </Modal>
        <Row style={{ marginBottom: 10, marginTop: 10 }}>
          <Col span={18}>
            <Button type="primary" onClick={this.cancelHold}>
              取消暂挂
            </Button>
          </Col>
          <Col span={6}>
            <Search
              placeholder="请输入单据编号"
              onSearch={e => {
                this.search(e);
              }}
              enterButton
            />
          </Col>
        </Row>
        <Table
          onRow={record => {
            return {
              onClick: event => {
                this.select(record, event);
              }, // 点击行
            };
          }}
          rowKey={record => record.id}
          rowSelection={rowSelection}
          dataSource={dataSource}
          columns={detailColumns}
          pagination={pagination}
          bordered
        />
      </div>
    );
  }
}

const WrappedTemporaryTap = Form.create({ name: 'temporarytap_reason' })(TemporaryTap);

export default connect()(WrappedTemporaryTap);
