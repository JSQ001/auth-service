import React from 'react';
import { Table, Row, Col, Button, Form, Input } from 'antd';
// import { routerRedux } from 'dva/router';
import { connect } from 'dva';
// import { messages } from 'utils/utils';
// import moment from 'moment';
// import FundEditablTable from '../../fund-components/fund-editable-table';
import FundSearchForm from '../../fund-components/fund-search-form';
import FundAreaDefinitionService from './fund-area-definition.service';

const selectWidth = { width: '100%' };

class FundAreaDefinition extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      //   copyDataSource: '',
      // >>>>>>>>>>>
      loading: false, // loading状态
      searchParams: {}, // 模糊查询数据搜索参数
      pagination: {
        total: 0,
        page: 0,
        pageSize: 10,
        current: 1,
      },
      searchForm: [
        {
          colSpan: 6,
          type: 'input',
          label: '行政区域代码',
          id: 'administrativeAreaCode',
        },
        {
          colSpan: 6,
          type: 'input',
          label: '地区名称',
          id: 'areaName',
        },
      ],
      columns: [
        {
          title: '行政区域代码',
          dataIndex: 'regionCode',
          editable: true,
          render: (value, record, index) => {
            // const { status } = this.state;
            console.log('record', record);
            return record.status === 'new' ? (
              <Input
                placeholder="请输入"
                defaultValue={record.regionCode ? record.regionCode : value}
                onChange={e => this.onDescChange(e, index, 'regionCode')}
                // style={{ textAlign: 'center' }}
                style={selectWidth}
              />
            ) : (
              <span>{record.regionCode}</span>
            );
          },
        },
        {
          title: '地区名称',
          dataIndex: 'description',
          editable: true,
          // render: (value, record, index) => {
          //     // const { status } = this.state;
          //     return (
          //       <Input
          //         placeholder="请输入"
          //         defaultValue={record.description ? record.description : value}
          //         onChange={e => this.onDescChange(e, index, 'description')}
          //         // style={{ textAlign: 'center' }}
          //         style={selectWidth}
          //       />
          //     )
          //     // : (
          //     //   <span>{record.regionCode}</span>
          //     // );
          // },
          // render: (enabled, record) => {
          //   const { employNameList } = this.state;
          //   return record.status === 'new' ? (
          //     <Select
          //       placeholder="请选择"
          //       style={selectWidth}
          //       labelInValue
          //       onChange={this.changeVllue}
          //     >
          //       {employNameList.length > 0 &&
          //         employNameList.map(empItem => {
          //           return (
          //             <Option key={empItem.value}>
          //               {empItem.name}-{empItem.value}
          //             </Option>
          //           );
          //         })}
          //     </Select>
          //   ) : (
          //     <span>{record.employeeLevelDesc}</span>
          //   );
          // },
        },
      ],
    };
  }

  componentDidMount() {
    this.getList();
  }

  /**
   * 列表数据
   */
  getList() {
    const { pagination, searchParams } = this.state;
    this.setState({ loading: true });
    FundAreaDefinitionService.getHead(pagination.page, pagination.pageSize, searchParams).then(
      response => {
        const { data } = response;
        this.setState({
          tableData: data,
          loading: false,
          pagination: {
            ...pagination,
            total: Number(response.headers['x-total-count'])
              ? Number(response.headers['x-total-count'])
              : 0,
            onChange: this.onChangePager,
            current: pagination.page + 1,
            onShowSizeChange: this.onShowSizeChange,
            showSizeChanger: true,
            showQuickJumper: true,
            showTotal: (total, range) =>
              this.$t('common.show.total', { range0: `${range[0]}`, range1: `${range[1]}`, total }),
          },
        });
      }
    );
    // .catch(err => {
    //   message.error(err);
    // });
  }

  /**
   * 更多展示中的搜索
   */
  handleSearch = values => {
    const { searchParams } = this.state;
    this.setState(
      {
        searchParams: {
          ...searchParams,
          regionCode: values.administrativeAreaCode, // 行政区域代码（input输入框）
          description: values.areaName, // 地区名称（input输入框）
        },
      },
      () => {
        this.getList();
      }
    );
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
        this.getList();
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
        this.getList();
      }
    );
  };

  /**
   * 新建
   */
  handleCreateClick = () => {};

  /**
   * 保存数据
   */
  //   saveTable = value => {
  //     const { tableData, parameterConfigurationData } = this.state;
  //     const saveValue = value.map((item, index) => {
  //       let resItem = {};
  //       if (tableData[index]) {
  //         resItem = {
  //           ...tableData[index],
  //           ...item,
  //         };
  //       } else {
  //         resItem = {
  //           ...item,
  //           headId: parameterConfigurationData.id,
  //         };
  //       }
  //       return resItem;
  //     });
  //     FundAreaDefinitionService.updateSave(saveValue).then(response => {
  //       if (response.status === 200) {
  //         message.success('保存成功');
  //       } else {
  //         // message.error(response.data.error.message);
  //         message.error('保存失败！');
  //       }
  //     });
  //   };

  render() {
    const { loading, columns, searchForm, pagination, tableData } = this.state;
    return (
      <div className="account-transfer-setting">
        <div className="common-top-area">
          <Row>
            <FundSearchForm
              submitHandle={this.handleSearch}
              searchForm={searchForm}
              //   maxLength={4}
            />
          </Row>
        </div>
        <div>
          <div style={{ margin: '10px 0' }}>
            <Row>
              <Col span={10}>
                <Button
                  onClick={this.handleCreateClick}
                  style={{ marginRight: '10px' }}
                  type="primary"
                >
                  {this.$t('common.create')}
                </Button>
                <Button onClick={this.handleSave} type="primary">
                  {this.$t('common.save')}
                </Button>
              </Col>
            </Row>
          </div>
          {/* <FundEditablTable
            columns={columns}
            dataSource={tableData}
            pagination={pagination}
            saveTable={this.saveTable}
            // cancel={this.cancel}
            needTopButton
            needBottomButton
          /> */}
          <Table
            rowKey={record => record.id}
            columns={columns}
            dataSource={tableData}
            bordered
            size="middle"
            pagination={pagination}
            loading={loading}
            onChange={this.onChangePager}
          />
        </div>
      </div>
    );
  }
}

function mapStateToProps(state) {
  return {
    user: state.user.currentUser,
    company: state.user.company,
  };
}
export default connect(mapStateToProps)(Form.create()(FundAreaDefinition));
