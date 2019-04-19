import React from 'react';
import {
  // Table,
  Row,
  Col,
  Button,
  Form,
  message,
} from 'antd';
import { connect } from 'dva';
// import { messages } from 'utils/utils';
import FundEditablTable from '../../fund-components/fund-editable-table';
import FundSearchForm from '../../fund-components/fund-search-form';
import FundAreaDefinitionService from './fund-area-definition.service';
import 'styles/fund/fund-area.scss';

class FundAreaDefinition extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      updateTableData: false, // 触发子组件的条件，模糊搜索时重新渲染页面
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
        },
        {
          title: '地区名称',
          dataIndex: 'description',
          editable: true,
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
    FundAreaDefinitionService.getHead(pagination.page, pagination.pageSize, searchParams).then(
      response => {
        const { data } = response;
        this.setState({
          tableData: data,
          pagination: {
            ...pagination,
            total: Number(response.headers['x-total-count'])
              ? Number(response.headers['x-total-count'])
              : 0,
            onChange: this.onChangePager,
            current: pagination.page + 1,
            pageSize: pagination.pageSize,
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
   * 模糊查询中的搜索按钮
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
        this.setState({ updateTableData: true });
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
    // console.log(temp);
    this.setState(
      {
        pagination: temp,
      },
      () => {
        this.setState({ updateTableData: true }); // 为了触发FundEditablTable组件，重新渲染刷新数据
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
        this.setState({ updateTableData: true });
        this.getList();
      }
    );
  };

  /**
   * 新增
   */
  handleAdd = () => {
    this.child.handleAdd();
  };

  /**
   * onRef调用子组件方法
   */
  onRef = ref => {
    this.child = ref;
  };

  /**
   * 保存saveTable
   */
  save = () => {
    this.child.saveTable();
  };

  /**
   * 保存数据
   */
  saveTable = value => {
    // value --> 选中所有行的值({regionCode、description})
    const { tableData } = this.state;
    const saveValue = value.map((item, index) => {
      let resItem = {};
      if (tableData[index]) {
        // 选中index行，并修改该行数据的保存
        resItem = {
          ...tableData[index],
          ...item,
        };
      } else {
        // 新增行数据的保存(无id，只有{regionCode、description})
        resItem = {
          ...item,
        };
      }
      return resItem;
    });
    // console.log('保存数据>>>>>>', saveValue);
    FundAreaDefinitionService.updateSave(saveValue).then(response => {
      if (response.status === 200) {
        message.success('保存成功');
        this.getList();
      } else {
        // message.error(response.data.error.message);
        message.error('保存失败！');
      }
    });
  };

  render() {
    const { columns, searchForm, pagination, tableData, updateTableData } = this.state;
    return (
      <div className="fund-area-definition">
        <div className="common-top-area">
          <Row>
            <FundSearchForm submitHandle={this.handleSearch} searchForm={searchForm} />
          </Row>
        </div>
        <div>
          <div style={{ margin: '10px 0' }}>
            <Row>
              <Col span={10}>
                <Button onClick={this.handleAdd} style={{ marginRight: '10px' }} type="primary">
                  {this.$t('common.create')}
                </Button>
                <Button onClick={this.save} type="primary">
                  {this.$t('common.save')}
                </Button>
              </Col>
            </Row>
          </div>
          <FundEditablTable
            columns={columns}
            dataSource={tableData}
            pagination={pagination}
            onRef={this.onRef}
            saveTable={this.saveTable}
            onTableChange={this.onChangePager}
            updateTableData={updateTableData}
            needTopButton
            needBottomButton
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
