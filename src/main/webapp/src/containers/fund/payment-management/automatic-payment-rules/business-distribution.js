import React from 'react';
// import config from 'config';
// import ListSelector from 'widget/list-selector';
import Table from 'widget/table';
import { routerRedux } from 'dva/router';
import {
  Form,
  Row,
  Col,
  Badge,
  Divider,
  Button,
  Checkbox,
  Select,
  Input,
  message,
  Icon,
} from 'antd';
import { connect } from 'dva';
import AutomaticPaymentRulesService from './automatic-payment-rules.service';

const { Search } = Input;
const { Option } = Select;
const selectWidth = { width: '100%' };

// const {
//     form: { getFieldDecorator },
// } = this.props;

class BusinessDistribution extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      loading: false,
      employList: {}, // 员工代码值列表
      employNameList: [], // 员工名称列表
      employTypeList: [
        { label: '规则描述', id: 'description' },
        { label: '付款公司', id: 'companyName' },
        { label: '付款账户', id: 'accountId' },
        { label: '启用', id: 'enabled' },
      ],
      employTypeInfo: {},
      columns: [
        {
          title: '业务类型代码',
          dataIndex: 'employeeLevelDesc',
          align: 'center',
          render: (enabled, record) => {
            const { employNameList } = this.state;
            return record.status === 'new' ? (
              <Select
                placeholder="请选择"
                style={selectWidth}
                labelInValue
                onChange={this.changeVllue}
              >
                {employNameList.length > 0 &&
                  employNameList.map(empItem => {
                    return (
                      <Option key={empItem.value}>
                        {empItem.name}-{empItem.value}
                      </Option>
                    );
                  })}
              </Select>
            ) : (
              <span>{record.employeeLevelDesc}</span>
            );
          },
        },
        {
          title: '业务类型名称',
          //   dataIndex: '',
          dataIndex: 'employeeLevel',
          align: 'center',
          render: (enabled, record) => {
            const { employList } = this.state;
            // console.log('--->', employList);
            return record.status === 'new' ? (
              //   <Select
              //     disabled
              //     style={selectWidth}
              //     labelInValue
              //     defaultValue={{key: employList.key}}
              //     onChange={this.changeVllue}
              //   >
              //     <Option value={employList.key}>{employList.key}</Option>
              //   </Select>
              <Input placeholder={employList.key} disabled AUTOCOMPLETE="off" />
            ) : (
              <span>{record.employeeLevel}</span>
            );
          },
        },
        {
          title: '备注',
          dataIndex: '',
          align: 'center',
          render: (enabled, record) => {
            return record.status === 'new' ? (
              <Input disabled />
            ) : (
              <span />
              //   <span>{record.employeeLevelDesc}</span>
            );
          },
        },
        {
          title: '启用',
          dataIndex: 'enabled',
          width: '8%',
          align: 'center',
          render: (enabled, record, index) => {
            return record.status === 'new' ? (
              <span>
                <a onClick={() => this.handleSave(index)}>保存</a>
                <Divider type="vertical" />
                <a onClick={() => this.handleCancel(index)}>取消</a>
              </span>
            ) : (
              <Checkbox
                defaultChecked={enabled}
                onChange={e => this.handleStatusChange(e, record)}
              />
            );
          },
        },
      ],
      data: [],
      dataTemp: [], // 临时存储data
      // defineId: this.props.match.params.id, // 当前类型定义的id ---用于保存，修改，获取baseInfo
      pagination: {
        total: 0,
        showQuickJumper: true,
        showSizeChanger: true,
      },
      page: 0,
      // pageSize: 10,
      // showListSelector: false,
    };
  }

  componentWillMount() {
    this.getBasicInfo();
    this.getList();
    this.getPaymentEmployOptions();
    const { match } = this.props;
    this.setState({
      defineId: match.params.id,
    });
  }

  /**
   * 选择的每一个option
   */
  changeVllue = value => {
    this.setState({
      employList: value,
    });
  };

  //
  getBasicInfo = () => {
    const { match } = this.props;
    AutomaticPaymentRulesService.getInfoById(match.params.id).then(res => {
      this.setState({ employTypeInfo: res.data });
    });
  };

  /**
   * 获取列表数据
   */
  getList = () => {
    const { match } = this.props;
    const { page } = this.state;
    this.setState({ loading: true });
    AutomaticPaymentRulesService.getDistributiveEmploy(match.params.id).then(res => {
      this.setState({
        data: res.data,
        loading: false,
        pagination: {
          total: Number(res.headers['x-total-count']) ? Number(res.headers['x-total-count']) : 0,
          current: page + 1,
          onChange: this.onChangePaper,
          onShowSizeChange: this.onShowSizeChange,
          showTotal: total => this.$t({ id: 'common.total' }, { total }),
        },
      });
    });
  };

  /**
   * 分配员工添加按钮
   */
  handAddEmploy = () => {
    const { data, dataTemp } = this.state;
    const value = {
      id: new Date().toJSON(),
      headerFlag: true,
      sequence: 10,
      status: 'new',
    };
    dataTemp[data.length] = value;
    data.push(value);
    this.setState({ data, dataTemp });
  };

  /**
   * 获取员工代码值列表
   */
  getPaymentEmployOptions = () => {
    this.getSystemValueList('1008')
      .then(res => {
        if (res.data.values.length > 0) {
          //   console.log('----employNameList--->', res.data.values);
          this.setState({
            employNameList: res.data.values,
          });
        }
      })
      .catch(err => {
        message.error(err.response.data.message);
      });
  };

  /**
   * 校验
   */
  canSaveOrNot = record => {
    // console.log('----recccccord---', record);
    if (!record.employeeLevel) {
      message.error('员工代码不能为空');
      return false;
    }
    return true;
  };

  /**
   * 新建时表格中的保存
   */
  handleSave = index => {
    const { data, dataTemp, defineId, employList } = this.state;
    const record = { ...data[index] };
    // console.log('=====save-->record===', record);
    const params = {
      employeeLevel: employList.key,
      //   employeeLevel: record.employeeLevel,
      employeeLevelDesc: record.employeeLevelDesc || '',
      //   companyType: record.companyType,
      enabled: record.enabled,
      headId: defineId,
    };
    const flag = this.canSaveOrNot(params);
    let handleMethod = null;
    let messageValue = '';
    if (!flag) return;
    if (record.status === 'new') {
      // 新增下的保存
      handleMethod = AutomaticPaymentRulesService.saveEmployValue;
      messageValue = '新增数据成功';
      //   debugger;
    }
    if (handleMethod) {
      handleMethod(params)
        .then(res => {
          message.success(messageValue);
          const tempObj = { ...res.data[0] }; // 接口返回的dimesionName，valueName 均null
          delete tempObj.employeeLevel;
          delete tempObj.valueName;
          data[index] = { ...record, ...tempObj, status: 'normal', id: res.data.id };
          dataTemp[index] = null;
          this.setState({ data, dataTemp });
          //   console.log('============');
          this.getList(); // 重新获取数据
        })
        .catch(err => {
          dataTemp[index] = { ...data[index] };
          this.setState({ dataTemp });
          message.error(err.response.data.message);
        });
    }
    // handleMethod && handleMethod(params)
    //     .then(res => {
    //       message.success(messageValue);
    //       const tempObj = { ...res.data[0] }; // 接口返回的dimesionName，valueName 均null
    //       delete tempObj.employeeLevel;
    //       delete tempObj.valueName;
    //       data[index] = { ...record, ...tempObj, status: 'normal', id: res.data.id };
    //       dataTemp[index] = null;
    //       this.setState({ data, dataTemp });
    //     })
    //     .catch(err => {
    //       dataTemp[index] = { ...data[index] };
    //       this.setState({ dataTemp });
    //       message.error(err.response.data.message);
    //     });
  };

  /**
   * 新建时表格中的取消
   */
  handleCancel = index => {
    if (Object.is(index, NaN) || Object.is(index, undefined)) return;
    const { data, dataTemp } = this.state;
    if (data[index].status === 'edit') {
      // 找到该行数据在dataTemp中的位置，重新拿出来,且在dataTmep中删除该数据
      data[index] = { ...dataTemp[index], status: 'normal' };
      dataTemp[index] = null;
      this.setState({ data, dataTemp });
    } else if (data[index].status === 'new') {
      // 新增情况下的取消
      data.splice(index, 1);
      dataTemp.splice(index, 1);
      this.setState({
        data,
        dataTemp,
      });
    }
  };

  /**
   * 切换每页显示的条数
   */
  onShowSizeChange = current => {
    this.setState(
      {
        page: current - 1,
        // pageSize,
      },
      () => {
        this.getList();
      }
    );
  };

  /**
   * 切换页面
   */
  onChangePaper = page => {
    // const { page } = this.state;
    if (page - 1 !== page) {
      this.setState({ page: page - 1 }, () => {
        this.getList();
      });
    }
  };

  /**
   *  选中启用状态框
   */
  handleStatusChange = (e, record) => {
    const params = {
      id: record.id,
      enabled: e.target.checked,
      versionNumber: record.versionNumber,
    };
    AutomaticPaymentRulesService.updateAssignEmploy(params)
      .then(() => {
        this.getList();
        message.success('操作成功');
      })
      .catch(() => {
        message.error(`${e.response.data.message}`);
      });
  };

  //
  //   handleListShow = flag => {
  //     this.setState({ showListSelector: flag });
  //   };

  //
  //   handleListOk = values => {
  //     if (!values.result || !values.result.length) {
  //       this.handleListShow(false);
  //       return;
  //     }
  //     const paramsValue = [];
  //     const {match} = this.props;
  //     paramsValue.sobPayReqTypeId = match.params.id;
  //     paramsValue.companyId = [];
  //     paramsValue.compcompanyCodeanyId = [];

  //     values = values.result.map(item => item.id);

  //     AutomaticPaymentRulesService
  //       .batchDistributeCompany(match.params.id, values)
  //       .then(() => {
  //         message.success('操作成功');
  //         this.handleListShow(false);
  //         this.getList();
  //       })
  //       .catch(e => {
  //         if (e.response) {
  //           message.error(`操作失败，${e.response.data.message}`);
  //         }
  //       });
  //   };

  /**
   * 返回上一页
   */
  handleBack = () => {
    const { dispatch } = this.props;
    dispatch(
      routerRedux.push({
        pathname: '/payment-management/automatic-payment-rules/automatic-payment-rules/',
      })
    );
  };

  render() {
    const {
      loading,
      employTypeList,
      employTypeInfo,
      pagination,
      columns,
      data,
      // paymentMethodOptions,
      // showListSelector,
      // selectorItem,
    } = this.state;
    const periodRow = [];
    const periodCol = [];
    employTypeList.map((item, index) => {
      if (index <= 2) {
        periodCol.push(
          <Col span={6} style={{ marginBottom: '15px' }} key={item.id}>
            <div style={{ color: '#989898' }}>{item.label}</div>
            <div style={{ wordWrap: 'break-word' }}>
              {item.id === 'setOfBooksName'
                ? employTypeInfo[item.id]
                  ? `${employTypeInfo.setOfBooksCode} - ${employTypeInfo.setOfBooksName}`
                  : '-'
                : employTypeInfo[item.id]}
            </div>
          </Col>
        );
      }
      if (index === 3) {
        periodCol.push(
          <Col span={6} style={{ marginBottom: '15px' }} key={item.id}>
            <div style={{ color: '#989898' }}>{item.label}</div>
            <Badge
              status={employTypeInfo[item.id] ? 'success' : 'error'}
              text={employTypeInfo[item.id] ? '启用' : '禁用'}
            />
          </Col>
        );
      }
      // index <= 2 &&
      // periodCol.push(
      //   <Col span={6} style={{ marginBottom: '15px' }} key={item.id}>
      //     <div style={{ color: '#989898' }}>{item.label}</div>
      //     <div style={{ wordWrap: 'break-word' }}>
      //       {item.id === 'setOfBooksName'
      //         ? employTypeInfo[item.id]
      //           ? `${employTypeInfo.setOfBooksCode  } - ${  employTypeInfo.setOfBooksName}`
      //           : '-'
      //         : employTypeInfo[item.id]}
      //     </div>
      //   </Col>
      // );
      //   index == 3 &&
      //     periodCol.push(
      //       <Col span={6} style={{ marginBottom: '15px' }} key={item.id}>
      //         <div style={{ color: '#989898' }}>{item.label}</div>
      //         <Badge
      //           status={employTypeInfo[item.id] ? 'success' : 'error'}
      //           text={employTypeInfo[item.id] ? '启用' : '禁用'}
      //         />
      //       </Col>
      //     );
      return 'AAA';
    });
    periodRow.push(
      <Row
        style={{ background: '#f7f7f7', padding: '20px 25px 0', borderRadius: '6px 6px 0 0' }}
        key="1"
      >
        {periodCol}
      </Row>
    );
    return (
      <div className="company-distribution" style={{ paddingBottom: 20 }}>
        {periodRow}
        <div className="table-header">
          <div className="table-header-buttons">
            <Row>
              <Col span={8}>
                <Button type="primary" onClick={() => this.handAddEmploy(true)}>
                  分配业务级别
                </Button>
              </Col>
              {/* 模糊查询 */}
              <Col span={8} offset={8}>
                <Search
                  placeholder="请输入员工级别代码/名称"
                  enterButton
                  onSearch={this.searchByEmploy}
                />
              </Col>
            </Row>
          </div>
        </div>
        <Table
          rowKey={record => record.id}
          columns={columns}
          dataSource={data}
          loading={loading}
          bordered
          pagination={pagination}
          size="middle"
        />
        {/* <ListSelector
          visible={showListSelector}
          onCancel={() => this.handleListShow(false)}
          selectorItem={selectorItem}
          onOk={this.handleListOk}
        /> */}
        <a style={{ fontSize: '14px', paddingBottom: '20px' }} onClick={this.handleBack}>
          <Icon type="rollback" style={{ marginRight: '5px' }} />返回
        </a>
      </div>
    );
  }
}

const wrappedCompanyDistribution = Form.create()(BusinessDistribution);

export default connect()(wrappedCompanyDistribution);
