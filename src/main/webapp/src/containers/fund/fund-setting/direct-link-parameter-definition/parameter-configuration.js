import React from 'react';
import { Col, Row, Badge, message, Button } from 'antd';
import FundEditablTable from '../../fund-components/fund-editable-table';
import service from './direct-link-parameter-definition.service';

class ParameterConfiguration extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      parameterConfigurationData: '',
      tableData: [],
      pagination: {
        total: 0,
        page: 0,
        pageSize: 10,
        current: 1,
      },
      columns: [
        {
          title: 'CODE',
          dataIndex: 'paramsCode',
          type: 'input',
          editable: true,
          width: 200,
        },
        {
          title: '描述',
          dataIndex: 'paramsDesc',
          type: 'input',
          editable: true,
          width: 200,
        },
        {
          title: '值',
          dataIndex: 'paramsValue',
          type: 'input',
          editable: true,
          width: 200,
        },
      ],
    };
  }

  componentWillMount() {
    const { data } = this.props;
    this.setState({
      parameterConfigurationData: data,
    });
  }

  componentDidMount() {
    this.getLine();
  }

  /**
   * 保存数据
   */
  saveTable = value => {
    const { tableData, parameterConfigurationData } = this.state;
    const saveValue = value.map((item, index) => {
      let resItem = {};
      if (tableData[index]) {
        resItem = {
          ...tableData[index],
          ...item,
        };
      } else {
        resItem = {
          ...item,
          headId: parameterConfigurationData.id,
        };
      }
      return resItem;
    });
    service.bankParamsLineSet(saveValue).then(response => {
      if (response.status === 200) {
        message.success('保存成功');
      } else {
        message.error(response.data.error.message);
      }
    });
  };

  /**
   *取消
   */
  cancel = () => {
    const { onClose } = this.props;
    onClose();
  };

  /**
   * 分页点击
   */
  onChangePager = pagination => {
    const temp = {};
    temp.page = pagination.current - 1;
    temp.current = pagination.current;
    temp.pageSize = pagination.pageSize;
    this.setState(
      {
        pagination: temp,
      },
      () => {
        this.getLine();
      }
    );
  };

  /**
   * 改变每页显示的条数
   */
  onShowSizeChange = (current, pageSize) => {
    const temp = {};
    temp.page = current - 1;
    temp.pageSize = pageSize;
    this.setState(
      {
        pagination: temp,
      },
      () => {
        this.getLine();
      }
    );
  };

  /**
   * 获取行数据
   */
  getLine = () => {
    const { parameterConfigurationData, pagination } = this.state;
    service.getLine(parameterConfigurationData.id).then(response => {
      if (response.status === 200) {
        this.setState({
          tableData: response.data,
          pagination: {
            ...pagination,
            total: Number(response.headers['x-total-count'])
              ? Number(response.headers['x-total-count'])
              : 0,
            current: pagination.page + 1,
            pageSize: pagination.pageSize,
            onChange: this.onChangePager,
            onShowSizeChange: this.onShowSizeChange,
            showSizeChanger: true,
            showQuickJumper: true,
            showTotal: (total, range) =>
              this.$t('common.show.total', { range0: `${range[0]}`, range1: `${range[1]}`, total }),
          },
        });
      }
    });
  };

  /**
   * onRef调用子组件方法
   */
  onRef = ref => {
    this.child = ref;
  };

  /**
   * 新增
   */
  handleAdd = () => {
    this.child.handleAdd();
  };

  /**
   * 保存saveTable
   */
  save = () => {
    this.child.saveTable();
  };

  render() {
    const { parameterConfigurationData, tableData, pagination, columns } = this.state;
    return (
      <div>
        <Row gutter={24} style={{ paddingBottom: '20px', borderBottom: '1px solid #e8e8e8' }}>
          <div style={{ fontSize: '14px' }}>
            <h1 style={{ marginLeft: '20px' }}>银行信息</h1>
            <Col offset={2} span={10} style={{ marginTop: '10px' }}>
              <span>银行名称:{parameterConfigurationData.bankName}</span>
            </Col>
            <Col offset={2} span={10} style={{ marginTop: '10px' }}>
              <span>银行代码:{parameterConfigurationData.bankCode}</span>
            </Col>
            <Col offset={2} span={10} style={{ marginTop: '10px' }}>
              <span>直联描述:{parameterConfigurationData.description}</span>
            </Col>
            <Col offset={2} span={10} style={{ marginTop: '10px' }}>
              状态:{parameterConfigurationData.enabled === 1 ? (
                <Badge status="success" text="启用" />
              ) : (
                <Badge status="error" text="禁用" />
              )}
            </Col>
          </div>
        </Row>
        <Row gutter={24} style={{ padding: '20px 0' }}>
          <section style={{ paddingTop: '20px', marginLeft: '20px' }}>
            <h1>参数配置</h1>
          </section>
          <section>
            <Button onClick={this.handleAdd} type="primary" style={{ margin: '16px 20px' }}>
              新增
            </Button>
          </section>

          <FundEditablTable
            columns={columns}
            dataSource={tableData}
            pagination={pagination}
            onRef={this.onRef}
            saveTable={this.saveTable}
            needTopButton
            needBottomButton
          />

          <section style={{ paddingTop: '50px', textAlign: 'center' }}>
            <div style={{ paddingTop: '10px', borderTop: '1px solid #e8e8e8' }}>
              <Button type="primary" style={{ marginRight: '10px' }} onClick={this.save}>
                保存
              </Button>
              <Button style={{ marginLeft: '10px' }} onClick={this.cancel}>
                取消
              </Button>
            </div>
          </section>
        </Row>
      </div>
    );
  }
}

export default ParameterConfiguration;
