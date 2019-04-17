import React from 'react';
import { Form, Row, Button, Badge } from 'antd';
import Table from 'widget/table';
import SlideFrame from 'widget/slide-frame';
import { connect } from 'dva';
import { routerRedux } from 'dva/router';
import FundSearchForm from '../../fund-components/fund-search-form';
import NewDefinition from './new-definition';
import ParameterConfiguration from './parameter-configuration';
import service from './direct-link-parameter-definition.service';
import 'styles/fund/account.scss';

class DirectLinkParameterDefinition extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      pagination: {
        total: 0,
        page: 0,
        pageSize: 10,
        current: 1,
      },
      loading: false,
      searchParams: {},
      tableData: [],
      slideVisible: false,
      currentOperation: '',
      searchForm: [
        {
          colSpan: 6,
          type: 'valueList',
          label: '银行名称',
          id: 'gatherBankNum',
          options: [],
          valueListCode: 'ZJ_OPEN_BANK',
        },
        {
          colSpan: 6,
          type: 'input',
          label: '直联描述',
          id: 'directDescription',
        },
        {
          colSpan: 6,
          type: 'enabledStateSelect',
          label: '状态',
          id: 'status',
          options: [
            {
              value: 1,
              name: '启用',
            },
            {
              value: 0,
              name: '禁用',
            },
          ],
          valueListCode: 'ZJ_STATUS',
        },
      ],
      columns: [
        {
          title: '银行名称',
          dataIndex: 'bankName',
          width: 200,
        },
        {
          title: '银行代码',
          dataIndex: 'bankCode',
          width: 200,
        },
        {
          title: '直联描述',
          dataIndex: 'description',
          width: 200,
        },
        {
          title: '参数配置',
          dataIndex: 'parameterConfiguration',
          width: 200,
          render: (record, index) => (
            <a
              onClick={event => {
                event.stopPropagation();
                this.parameterConfiguration(index);
              }}
            >
              配置
            </a>
          ),
        },
        {
          title: '状态',
          dataIndex: 'enabled',
          width: 200,
          render: record =>
            record === 1 ? (
              <Badge status="success" text="启用" />
            ) : (
              <Badge status="error" text="禁用" />
            ),
        },
        {
          title: '操作',
          dataIndex: 'operating',
          width: 200,
          render: (record, index) => (
            <a
              onClick={event => {
                event.stopPropagation();
                this.operating(index);
              }}
            >
              公司分配
            </a>
          ),
        },
      ],
    };
  }

  componentDidMount() {
    this.getHead();
  }

  /**
   * 获取头基础信息
   */
  getHead = () => {
    const { pagination, searchParams } = this.state;
    this.setState({ loading: true });
    service.getHead(pagination.page, pagination.pageSize, searchParams).then(response => {
      this.setState({
        tableData: response.data,
        loading: false,
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
    });
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
        this.getHead();
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
        this.getHead();
      }
    );
  };

  /**
   * 参数配置
   */
  parameterConfiguration = value => {
    this.setState({
      parameterConfigurationData: value,
      slideVisible: true,
      currentOperation: 'parameterConfiguration',
    });
  };

  /**
   * 操作
   */
  operating = value => {
    const { dispatch } = this.props;
    dispatch(
      routerRedux.push({
        pathname: `/fund/fund-setting/direct-link-parameter-definition/company-allocation/${
          value.id
        }`,
      })
    );
  };

  /**
   * 搜索
   */
  handleSearch = value => {
    const { searchParams } = this.state;
    this.setState(
      {
        searchParams: {
          ...searchParams,
          bankCode: value.gatherBankNum ? value.gatherBankNum.key : '',
          description: value.directDescription,
          enabled: value.status ? value.status.key : '',
        },
      },
      () => {
        this.getHead();
      }
    );
  };

  /**
   * 新建
   */
  create = () => {
    this.setState({
      slideVisible: true,
      currentOperation: 'create',
    });
  };

  /**
   * 侧边栏关闭
   */
  slideFrameClose = () => {
    this.setState({
      slideVisible: false,
    });
  };

  /**
   * 渲染当前操作组件
   */
  renderCurrentOperation = value => {
    const { parameterConfigurationData } = this.state;
    switch (value) {
      case 'create':
        return <NewDefinition onClose={() => this.slideFrameClose()} />;
      case 'parameterConfiguration':
        return (
          <ParameterConfiguration
            data={parameterConfigurationData}
            onClose={() => this.slideFrameClose()}
          />
        );
      default:
        return <NewDefinition onClose={() => this.slideFrameClose()} />;
    }
  };

  render() {
    const {
      searchForm,
      columns,
      tableData,
      loading,
      pagination,
      slideVisible,
      currentOperation,
    } = this.state;
    return (
      <div className="train">
        <div className="common-top-area">
          <Row>
            <FundSearchForm submitHandle={this.handleSearch} searchForm={searchForm} />
          </Row>
        </div>
        <div className="table-header">
          <div className="table-header-buttons">
            <Row>
              <Button
                type="primary"
                onClick={e => {
                  e.preventDefault();
                  e.stopPropagation();
                  this.create();
                }}
              >
                新建
              </Button>
            </Row>
          </div>
          <Table
            columns={columns}
            dataSource={tableData}
            pagination={pagination}
            loading={loading}
          />
        </div>
        {/* 侧边栏 */}
        <SlideFrame title="新建" show={slideVisible} onClose={() => this.slideFrameClose()}>
          {this.renderCurrentOperation(currentOperation)}
        </SlideFrame>
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

export default connect(mapStateToProps)(Form.create()(DirectLinkParameterDefinition));
