import React from 'react';
import {
  Form,
  Row,
  Col,
  Badge,
  Button,
  message,
  Icon,
  Divider,
  Popconfirm,
  Select,
  InputNumber,
} from 'antd';
import Table from 'widget/table';
import Service from './service';
import { routerRedux } from 'dva/router';
import { connect } from 'dva';

const Option = Select.Option;
const selectWidth = { width: '100%' };

class DimensionSetting extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      loading: false,
      companyTypeList: [
        { label: this.$t({ id: 'pre.payment.setOfBookName' }), id: 'setOfBooksName' },
        { label: this.$t('billing.type.code'), id: 'reportTypeCode' },
        { label: this.$t('billing.type.name'), id: 'reportTypeName' },
        { label: this.$t('common.column.status'), id: 'enabled' },
      ],
      companyTypeInfo: {},
      columns: [
        {
          title: this.$t('exp.dimension'),
          dataIndex: 'dimensionId',
          align: 'center',
          render: (value, record, index) => {
            return record.status === 'new' || record.status === 'edit' ? (
              <Select
                placeholder={this.$t('common.please.select')}
                style={selectWidth}
                value={{ key: record.dimensionId || '', label: record.dimensionName }}
                labelInValue
                disabled={record.status === 'edit' ? true : false}
                onChange={value => this.handleDimensionChange(value, index, 'dimensionId')}
                onDropdownVisibleChange={this.getUndistributedDimension}
              >
                {this.state.dimensionList.length > 0 &&
                  this.state.dimensionList.map(dimeItem => {
                    return <Option key={dimeItem.id}>{dimeItem.dimensionName}</Option>;
                  })}
              </Select>
            ) : (
              <span>{record.dimensionName ? record.dimensionName : '-'}</span>
            );
          },
        },
        {
          title: this.$t('exp.input.or.not'),
          dataIndex: 'mustEnter',
          align: 'center',
          render: (value, record, index) => {
            index = index + this.state.pagination.page * this.state.pagination.pageSize;
            return record.status === 'new' || record.status === 'edit' ? (
              <Select
                style={selectWidth}
                placeholder={this.$t('common.please.select')}
                defaultValue={value ? this.$t('exp.must.input') : this.$t('exp.must.not.input')}
                onChange={value => this.handleValueChange(value, index, 'mustEnter')}
              >
                <Option key="必输">{this.$t('exp.must.input')}</Option>
                <Option key="非必输">{this.$t('exp.must.not.input')}</Option>
              </Select>
            ) : (
              <span>
                {record.mustEnter ? this.$t('exp.must.input') : this.$t('exp.must.not.input')}
              </span>
            );
          },
        },
        {
          title: this.$t('exp.default.value'),
          dataIndex: 'defaultValueName',
          align: 'center',
          render: (value, record, index) => {
            return record.status === 'new' || record.status === 'edit' ? (
              <Select
                placeholder={this.$t('common.please.select')}
                disabled={!record.dimensionId || record.dimensionId == ''}
                style={selectWidth}
                allowClear={true}
                value={{ key: record.defaultValueId || '', label: record.defaultValueName }}
                labelInValue
                onChange={value => this.handleValueChange(value, index, 'defaultValueName')}
                // onDropdownVisibleChange={(open) => open && this.focusToGetValue(index)}
              >
                {this.state.defaultValueList[index] &&
                  this.state.defaultValueList[index].length > 0 &&
                  this.state.defaultValueList[index].map(valueItem => {
                    return (
                      <Option key={valueItem.id} value={valueItem.id}>
                        {valueItem.dimensionItemName}
                      </Option>
                    );
                  })}
              </Select>
            ) : (
              <span>{record.defaultValueName ? record.defaultValueName : ''}</span>
            );
          },
        },
        {
          title: this.$t('exp.position') /**'布局位置' */,
          dataIndex: 'position',
          align: 'center',
          render: (value, record, index) => {
            return record.status === 'new' || record.status === 'edit' ? (
              <Select
                style={selectWidth}
                placeholder={this.$t('common.please.select')}
                defaultValue={
                  value == 'DIST_LINE' ? this.$t('exp.dist.line') : this.$t('exp.header')
                }
                onChange={value => this.handleValueChange(value, index, 'position')}
              >
                <Option key="HEADER" value="HEADER">
                  {this.$t('exp.header')}
                </Option>
                <Option key="DIST_LINE" value="DIST_LINE">
                  {this.$t('exp.dist.line')}
                </Option>
              </Select>
            ) : (
              <span>
                {record.id && record.position === 'DIST_LINE'
                  ? this.$t('exp.dist.line')
                  : this.$t('exp.header')}
              </span>
            );
          },
        },
        {
          title: this.$t('expense.policy.priority') /**优先级 */,
          dataIndex: 'sequenceNumber',
          align: 'center',
          render: (value, record, index) => {
            return record.status === 'new' || record.status === 'edit' ? (
              <InputNumber
                placeholder={this.$t('common.please.select')}
                defaultValue={value}
                // min={10}
                onChange={value => this.handleValueChange(value, index, 'sequenceNumber')}
              />
            ) : (
              <span>{record.sequenceNumber}</span>
            );
          },
        },
        {
          title: this.$t('expense.policy.options'),
          dataIndex: 'options',
          width: 140,
          align: 'center',
          render: (value, record, index) => {
            return record.status === 'new' || record.status === 'edit' ? (
              <span>
                <a onClick={() => this.handleSave(index)}>
                  {' '}
                  {this.$t({ id: 'common.save' }) /**保存 */}
                </a>
                <Divider type="vertical" />
                <a onClick={() => this.handleCancel(index)}>
                  {this.$t({ id: 'common.cancel' }) /* 取消 */}
                </a>
              </span>
            ) : (
              <span>
                <a onClick={() => this.handleEdit(index)}>{this.$t('common.edit')}</a>
                <Divider type="vertical" />
                <Popconfirm
                  title={this.$t({ id: 'common.confirm.delete' }) /* 确定删除吗？ */}
                  onConfirm={() => this.handleDelete(record.id)}
                  okText={this.$t({ id: 'common.ok' }) /* 确定 */}
                  cancelText={this.$t({ id: 'common.cancel' }) /* 取消 */}
                  placement="topRight"
                >
                  <a>{this.$t({ id: 'common.delete' }) /* 取消 */}</a>
                </Popconfirm>
              </span>
            );
          },
        },
      ],
      data: [],
      //临时存储data
      dataTemp: [],
      //维度的option数据
      dimensionList: [],
      //默认值的option数据
      // defaultValueList: [],
      defaultValueList: {},
      //当前类型定义的id ---用于保存，修改，获取baseInfo;1082849104776761346
      defineId: this.props.match.params.id,
      pagination: {
        page: 0,
        pageSize: 10,
        showQuickJumper: true,
        showSizeChanger: true,
      },
      page: 0,
      pageSize: 10,
    };
  }

  componentWillMount() {
    this.getBasicInfo();
    this.getList();
  }

  // componentDidMount() {
  //   this.getDimension();
  // }

  //获取未分配的维度
  getUndistributedDimension = value => {
    if (value) {
      const params = {
        setOfBooksId: this.state.companyTypeInfo.setOfBooksId,
        enabled: true,
      };
      const { match } = this.props;
      Service.getReportTypeDimensionList(match.params.id, params)
        .then(res => {
          this.setState({
            dimensionList: res.data,
          });
        })
        .catch(err => {
          message.error(err.response.data.message);
        });
    }
  };

  //获取维度
  getDimension = () => {
    const params = {
      page: 0,
      size: 1000,
      setOfBooksId: this.state.companyTypeInfo.setOfBooksId,
      enabled: true,
    };
    Service.getDimensionList(params)
      .then(res => {
        this.setState({
          dimensionList: res.data,
        });
      })
      .catch(err => {
        message.error(err.response.data.message);
      });
  };

  //级联默认值，维度
  handleDimensionChange = (value, index, dataIndex) => {
    let data = this.state.data;
    data[index][dataIndex] = value.key;
    data[index].dimensionName = value.label;
    this.setState({ data }, () => {
      this.getDefaultValue(value.key, index);
    });
  };

  //聚焦获取默认值，将出现bug：编辑时聚焦会将选中的options清除，此时如果取消，则导致默认值无值
  focusToGetValue = index => {
    this.getDefaultValue(this.state.data[index].dimensionId, index);
  };

  //获取默认维值
  getDefaultValue = (dimensionId, index) => {
    const { defaultValueList } = this.state;
    Service.getDimensionValueList(dimensionId)
      .then(res => {
        let temp = res.data;
        //当维值发生改变的同时，将默认值设置为查询到的数组中第一个值
        const { data, dataTemp } = this.state;
        let record = data[index];
        // let tempValue = temp.length > 0 ?
        //   {key: temp[0].id, label: temp[0].dimensionItemName} : {key: ''};
        let tempValue = { key: '' };

        record['defaultValueId'] = tempValue.key;

        dataTemp[index]['defaultValueId'] = tempValue.key;
        dataTemp[index]['defaultValueName'] = tempValue.label;

        defaultValueList[index] = temp;
        this.setState({ defaultValueList: defaultValueList, data: data });
      })
      .catch(err => {
        message.error(err.response.data.message);
      });
  };

  //监听form控件值的改变
  handleValueChange = (value, index, dataIndex) => {
    let { dataTemp, data } = this.state;
    if (dataIndex == 'position') {
      data[index][dataIndex] = value === 'DIST_LINE' ? 'DIST_LINE' : 'HEADER';
    } else if (dataIndex == 'mustEnter') {
      data[index][dataIndex] = value === this.$t('exp.must.input') ? true : false;
    } else if (dataIndex == 'defaultValueName') {
      let data = this.state.data; //由于设置了value，造成主动更改时option不变
      data[index][dataIndex] = value ? value.key : null;
      data[index]['defaultValueName'] = value ? value.label : null;
      data[index]['defaultValueId'] = value ? value.key : null;
    } else {
      data[index][dataIndex] = value;
    }

    this.setState({ data });
  };

  //校验
  canSaveOrNot = record => {
    if (!record.dimensionId) {
      message.error(this.$t('exp.dimension.can.not.empty'));
      return false;
    }
    return true;
  };

  //保存
  handleSave = index => {
    const { data, defineId, dataTemp } = this.state;
    const record = { ...data[index] };
    const { match } = this.props;
    let params = {
      reportTypeId: match.params.id,
      dimensionId: record.dimensionId,
      defaultValueId: record.defaultValueId,
      position: record.position,
      sequenceNumber: record.sequenceNumber,
      mustEnter: record.mustEnter,
    };
    let flag = this.canSaveOrNot(params);
    let handleMethod = null;
    let messageValue = '';
    if (!flag) return;
    if (record.status == 'new') {
      //新增下的保存
      handleMethod = Service.saveDimensionValue;
      messageValue = this.$t('exp.create.success');
    } else if (record.status == 'edit') {
      //修改下的保存
      params.id = record.id;
      params.versionNumber = record.versionNumber;
      handleMethod = Service.editDimensionValue;
      messageValue = this.$t('common.update.success');
    }

    handleMethod &&
      handleMethod(params)
        .then(res => {
          message.success(messageValue);
          let tempObj = { ...res.data[0] }; //接口返回的dimesionName，defaultValueName 均null
          delete tempObj['dimensionName'];
          delete tempObj['defaultValueName'];
          data[index] = { ...record, ...tempObj, status: 'normal', id: res.data.id };
          dataTemp[index] = null;
          this.setState({ data, dataTemp });
          this.getList();
        })
        .catch(err => {
          data[index] = { ...dataTemp[index] };
          this.setState({ data });
          message.error(err.response.data.message);
        });
  };

  //删除
  handleDelete = id => {
    Service.deleteDimensionValue(id)
      .then(res => {
        message.success(this.$t('common.delete.success'));
        this.getList();
      })
      .catch(err => {
        message.error(err.response.data.message);
      });
  };

  //编辑状态
  handleEdit = index => {
    const { data, dataTemp } = this.state;
    let record = data[index];
    //编辑状态下需获取一次默认值列
    this.getDValueListWhenEditing(record.dimensionId, index);
    record.status = 'edit';
    dataTemp[index] = { ...record };
    this.setState({ data, dataTemp });
  };

  //编辑状态下获取一次默认的维值列
  getDValueListWhenEditing = (dimensionId, index) => {
    if (dimensionId == undefined || dimensionId == '') return;
    const { defaultValueList } = this.state;
    Service.getDimensionValueList(dimensionId)
      .then(res => {
        defaultValueList[index] = res.data;
        this.setState({ defaultValueList });
      })
      .catch(err => {
        message.error(this.$t('acp.getData.error'));
      });
  };

  //取消
  handleCancel = index => {
    if (Object.is(index, NaN) || Object.is(index, undefined)) return;
    const { data, dataTemp } = this.state;
    if (data[index].status == 'edit') {
      //找到该行数据在dataTemp中的位置，重新拿出来,且在dataTmep中删除该数据
      data[index] = { ...dataTemp[index], status: 'normal' };
      dataTemp[index] = null;
      this.setState({ data, dataTemp });
    } else if (data[index].status == 'new') {
      //新增情况下的取消
      data.splice(index, 1);
      dataTemp.splice(index, 1);
      this.setState({
        data,
        dataTemp,
      });
    }
  };

  //获取账套等基础数据
  getBasicInfo = () => {
    const { match } = this.props;
    Service.getInfoById(match.params.id).then(res => {
      this.setState(
        {
          companyTypeInfo: res.data.expenseReportType,
          // setOfBooksId: res.data.applicationType.setOfBooksId
        },
        () => {
          //this.getDimension();
        }
      );
    });
  };

  getList = () => {
    const { match } = this.props;
    this.setState({ loading: true });
    Service.getDimensionById(match.params.id)
      .then(res => {
        this.setState({
          data: res.data,
          loading: false,
        });
      })
      .catch(err => {
        this.setState({ loading: false }, () => {
          message.error(err.response.message.data);
        });
      });
  };

  //添加维度
  handleListShow = flag => {
    // this.setState({ showListSelector: flag });
    const { data, dataTemp } = this.state;
    let value = {
      id: new Date().toJSON(),
      mustEnter: false,
      position: 'DIST_LINE',
      sequenceNumber: 10,
      status: 'new',
    };
    dataTemp[data.length] = value;
    data.push(value);
    this.setState({ data, dataTemp });
  };

  handleBack = () => {
    this.props.dispatch(
      routerRedux.push({
        pathname: `/document-type-manage/definition-billing-type/definition-billing-type`,
      })
    );
  };

  render() {
    const { loading, companyTypeList, companyTypeInfo, columns, data } = this.state;
    let periodRow = [];
    let periodCol = [];
    companyTypeList.map((item, index) => {
      index <= 2 &&
        periodCol.push(
          <Col span={6} style={{ marginBottom: '15px' }} key={item.id}>
            <div style={{ color: '#989898' }}>{item.label}</div>
            <div style={{ wordWrap: 'break-word' }}>
              {item.id === 'setOfBooksName'
                ? companyTypeInfo[item.id]
                  ? companyTypeInfo.setOfBooksCode + ' - ' + companyTypeInfo.setOfBooksName
                  : '-'
                : companyTypeInfo[item.id]}
            </div>
          </Col>
        );
      index == 3 &&
        periodCol.push(
          <Col span={6} style={{ marginBottom: '15px' }} key={item.id}>
            <div style={{ color: '#989898' }}>{item.label}</div>
            <Badge
              status={companyTypeInfo[item.id] ? 'success' : 'error'}
              text={
                companyTypeInfo[item.id] ? this.$t('common.enabled') : this.$t('common.disabled')
              }
            />
          </Col>
        );
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
            <Button type="primary" onClick={() => this.handleListShow(true)}>
              {this.$t('exp.dimension.to.add')}
            </Button>
          </div>
        </div>
        <Table
          loading={loading}
          rowKey={record => record.id}
          columns={columns}
          dataSource={data}
          size="middle"
          pagination={false}
        />
        <a style={{ fontSize: '14px', paddingBottom: '20px' }} onClick={this.handleBack}>
          <Icon type="rollback" style={{ marginRight: '5px' }} />
          {this.$t('pay.backlash.goback')}
        </a>
      </div>
    );
  }
}

const wrappedDimensionSetting = Form.create()(DimensionSetting);

export default connect()(wrappedDimensionSetting);
