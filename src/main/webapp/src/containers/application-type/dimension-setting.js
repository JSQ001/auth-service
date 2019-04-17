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
import service from './service';
import { routerRedux } from 'dva/router';
import { connect } from 'dva';

const Option = Select.Option;
const selectWidth = { width: '100%' };

class CompanyDistribution extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      loading: false,
      companyTypeList: [
        { label: '账套', id: 'setOfBooksName' },
        { label: '申请单类型代码', id: 'typeCode' },
        { label: '申请单类型名称', id: 'typeName' },
        { label: '状态', id: 'enabled' },
      ],
      companyTypeInfo: {},
      columns: [
        {
          title: '维度',
          dataIndex: 'dimensionId',
          align: 'center',
          render: (value, record, index) => {
            return record.status === 'new' || record.status === 'edit' ? (
              <Select
                placeholder="请选择"
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
          title: '默认值',
          dataIndex: 'defaultValue',
          align: 'center',
          render: (value, record, index) => {
            return record.status === 'new' || record.status === 'edit' ? (
              <Select
                placeholder="请选择"
                disabled={!record.dimensionId || record.dimensionId == ''}
                style={selectWidth}
                value={{ key: record['defaultValue'] || '', label: record.valueName || '' }}
                labelInValue
                allowClear
                onChange={value => this.handleValueChange(value, index, 'defaultValue')}
                // onDropdownVisibleChange={(open) => open && this.focusToGetValue(index)}
              >
                {this.state.defaultValueList[index] &&
                  this.state.defaultValueList[index].length > 0 &&
                  this.state.defaultValueList[index].map(valueItem => {
                    return <Option key={valueItem.id}>{valueItem.dimensionItemName}</Option>;
                  })}
              </Select>
            ) : (
              <span>{record.valueName ? record.valueName : ''}</span>
            );
          },
        },
        {
          title: '布局位置',
          dataIndex: 'headerFlag',
          align: 'center',
          render: (value, record, index) => {
            return record.status === 'new' || record.status === 'edit' ? (
              <Select
                style={selectWidth}
                placeholder="请选择"
                defaultValue={value ? '单据头' : '申请行'}
                onChange={value => this.handleValueChange(value, index, 'headerFlag')}
              >
                <Option key="单据头">单据头</Option>
                <Option key="申请行">申请行</Option>
              </Select>
            ) : (
              <span>{record.headerFlag ? '单据头' : '申请行'}</span>
            );
          },
        },
        {
          title: '是否必输',
          dataIndex: 'requiredFlag',
          align: 'center',
          render: (value, record, index) => {
            return record.status === 'new' || record.status === 'edit' ? (
              <Select
                style={selectWidth}
                placeholder="请选择"
                defaultValue={value ? '是' : '否'}
                onChange={value => this.handleValueChange(value, index, 'requiredFlag')}
              >
                <Option key="是">是</Option>
                <Option key="否">否</Option>
              </Select>
            ) : (
              <span>{record.requiredFlag ? '是' : '否'}</span>
            );
          },
        },
        {
          title: '优先级',
          dataIndex: 'sequence',
          align: 'center',
          render: (value, record, index) => {
            return record.status === 'new' || record.status === 'edit' ? (
              <InputNumber
                placeholder="请输入"
                defaultValue={value}
                min={0}
                onChange={value => this.handleValueChange(value, index, 'sequence')}
              />
            ) : (
              <span>{record.sequence}</span>
            );
          },
        },
        {
          title: '操作',
          dataIndex: 'options',
          width: 140,
          align: 'center',
          render: (value, record, index) => {
            return record.status === 'new' || record.status === 'edit' ? (
              <span>
                <a onClick={() => this.handleSave(index)}>保存</a>
                <Divider type="vertical" />
                <a onClick={() => this.handleCancel(index)}>取消</a>
              </span>
            ) : (
              <span>
                <a onClick={() => this.handleEdit(index)}>编辑</a>
                <Divider type="vertical" />
                <Popconfirm
                  title="确定删除"
                  onConfirm={() => this.handleDelete(record.id)}
                  okText="确定"
                  cancelText="取消"
                  placement="topRight"
                >
                  <a>删除</a>
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
      service
        .getUndistributedDimensionList(match.params.id, params)
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
    service
      .getDimensionList(params)
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
    service
      .getDimensionValueList(dimensionId)
      .then(res => {
        let temp = res.data;
        //当维值发生改变的同时，将默认值设置为查询到的数组中第一个值
        const { data, dataTemp } = this.state;
        let record = data[index];
        // let tempValue = temp.length > 0 ?
        //   {key: temp[0].id, label: temp[0].dimensionItemName} : {key: ''};
        let tempValue = { key: '' };

        record['defaultValue'] = tempValue.key;

        dataTemp[index]['defaultValue'] = tempValue.key;
        dataTemp[index]['valueName'] = tempValue.label;

        defaultValueList[index] = temp;
        this.setState({ defaultValueList, data });
      })
      .catch(err => {
        message.error(err.response.data.message);
      });
  };

  //监听form控件值的改变
  handleValueChange = (value, index, dataIndex) => {
    let { dataTemp, data } = this.state;

    if (dataIndex == 'headerFlag') {
      data[index][dataIndex] = value == '单据头' ? true : false;
      // dataTemp[index][dataIndex] = value == '单据头' ? true : false;
    } else if ('requiredFlag' == dataIndex) {
      data[index][dataIndex] = value == '是' ? true : false;
    } else if (dataIndex == 'defaultValue') {
      if (value) {
        let data = this.state.data; //由于设置了value，造成主动更改时option不变
        data[index][dataIndex] = value.key;
        data[index]['valueName'] = value.label;
        // dataTemp[index][dataIndex] = value.key;
        // dataTemp[index]['valueName'] = value.label;
        // this.setState({ data });
      } else {
        data[index][dataIndex] = '';
        data[index]['valueName'] = '';
      }
    } else {
      //dataTemp[index][dataIndex] = value;
      data[index][dataIndex] = value;
    }
    this.setState({ data });
  };

  //校验
  canSaveOrNot = record => {
    if (!record.dimensionId) {
      message.error('维度不能为空');
      return false;
    }
    return true;
  };

  //保存
  handleSave = index => {
    const { data, defineId, dataTemp } = this.state;
    const record = { ...data[index] };
    let params = [
      {
        dimensionId: record.dimensionId,
        defaultValue: record.defaultValue || '',
        headerFlag: record.headerFlag,
        sequence: record.sequence,
        requiredFlag: record.requiredFlag,
      },
    ];
    let flag = this.canSaveOrNot(params[0]);
    let handleMethod = null;
    let messageValue = '';
    if (!flag) return;
    if (record.status == 'new') {
      //新增下的保存
      handleMethod = service.saveDimensionValue;
      messageValue = '新增数据成功';
    } else if (record.status == 'edit') {
      //修改下的保存
      params[0].id = record.id;
      handleMethod = service.editDimensionValue;
      messageValue = '修改数据成功';
    }
    handleMethod &&
      handleMethod(defineId, params)
        .then(res => {
          message.success(messageValue);
          let tempObj = { ...res.data[0] }; //接口返回的dimesionName，valueName 均null
          delete tempObj['dimensionName'];
          delete tempObj['valueName'];
          data[index] = { ...record, ...tempObj, status: 'normal' };
          dataTemp[index] = null;
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
    service
      .deleteDimensionValue(id)
      .then(res => {
        message.success('成功删除数据');
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

    service
      .getDimensionValueList(dimensionId)
      .then(res => {
        defaultValueList[index] = res.data;
        this.setState({ defaultValueList });
      })
      .catch(err => {
        message.error('编辑状态下获取默认值失败');
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
    service.getInfoById(match.params.id).then(res => {
      this.setState(
        {
          companyTypeInfo: res.data.applicationType,
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

    service
      .getDimensionById(match.params.id)
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
      headerFlag: true,
      sequence: 10,
      status: 'new',
      requiredFlag: false,
    };
    dataTemp[data.length] = value;
    data.push(value);
    this.setState({ data, dataTemp });
  };

  handleBack = () => {
    this.props.dispatch(
      routerRedux.push({
        pathname: `/document-type-manage/application-type/application-type/${
          this.state.companyTypeInfo.setOfBooksId
        }`,
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
              {item.id === 'setOfBookCode'
                ? companyTypeInfo[item.id]
                  ? companyTypeInfo[item.id] + ' - ' + companyTypeInfo.setOfBookName
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
              text={companyTypeInfo[item.id] ? '启用' : '禁用'}
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
              添加维度
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
          <Icon type="rollback" style={{ marginRight: '5px' }} />返回
        </a>
      </div>
    );
  }
}

const wrappedCompanyDistribution = Form.create()(CompanyDistribution);

export default connect()(wrappedCompanyDistribution);
