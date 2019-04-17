import React from 'react';
import { connect } from 'dva';
import config from 'config';
import { Badge, Button, Popover, Row, Col } from 'antd';
import { routerRedux } from 'dva/router';
// import httpFetch from 'share/httpFetch';
import SlideFrame from 'widget/slide-frame';
import SearchArea from 'widget/search-area';
import CustomTable from 'widget/custom-table';
import baseService from 'share/base.service';
import FundDocumentsAdd from './fund-documents-add';

// const { Search } = Input;

class FundDocumentsType extends React.Component {
  constructor(props) {
    super(props);
    const { company } = this.props;
    this.state = {
      //   loading: true,
      //   data: [],
      //   page: 0,
      //   pageSize: 10,
      columns: [
        {
          title: '账套',
          dataIndex: 'setOfBooksName',
          width: '20%',
          align: 'center',
          render: (value, record) => {
            return (
              <span>
                <Popover content={`${record.setOfBooksCode}-${record.setOfBooksName}`}>
                  {record ? `${record.setOfBooksCode}-${record.setOfBooksName}` : '-'}
                </Popover>
              </span>
            );
          },
        },
        {
          title: '单据类型代码',
          dataIndex: 'acpReqTypeCode',
          width: '20%',
          align: 'center',
          render: record => (
            <span>
              <Popover content={record}>{record || '-'}</Popover>
            </span>
          ),
        },
        {
          title: '单据类型名称',
          dataIndex: 'description',
          width: '20%',
          align: 'center',
          render: description => <Popover content={description}>{description || '-'}</Popover>,
        },

        {
          title: '关联表单类型',
          dataIndex: 'formName',
          width: '20%',
          align: 'center',
          render: recode => (
            <span>
              <Popover content={recode}>{recode || '-'}</Popover>
            </span>
          ),
        },
        {
          title: '状态',
          dataIndex: 'enabled',
          width: '15%',
          align: 'center',
          render: isEnabled => (
            <Badge status={isEnabled ? 'success' : 'error'} text={isEnabled ? '启用' : '禁用'} />
          ),
        },
        {
          title: '操作',
          key: 'operation',
          width: '15%',
          align: 'center',
          render: (text, record) => (
            <span>
              <a onClick={e => this.editItem(e, record)}>编辑</a>
              <span className="ant-divider" />
              <a onClick={() => this.handleDistribute(record)}>公司分配</a>
            </span>
          ),
        }, // 操作
      ],
      //   pagination: { total: 0 },
      searchForm: [
        // 账套
        {
          type: 'select',
          id: 'setOfBooksId',
          label: '账套',
          options: [],
          labelKey: 'name',
          valueKey: 'id',
          isRequired: true,
          event: 'SETOFBOOKID',
          defaultValue: company.setOfBooksId,
          colSpan: '6',
        },
        { type: 'input', id: 'acpReqTypeCode', label: '单据类型代码', colSpan: '6' },
        { type: 'input', id: 'description', label: '单据类型名称', colSpan: '6' },
      ],
      searchParams: {
        setOfBooksId: company.setOfBooksId,
        acpReqTypeCode: '',
        description: '',
      },
      showSlideFrame: false,
      slideParams: {}, // 点击编辑时，存放该行的数据
      companyDistribution:
        '/document-type-manage/payment-requisition-type/distribution-company/:setOfBooksId/:id', // 公司分配
    };
  }

  componentWillMount() {
    this.getSetOfBookList();
  }

  /**
   * 获取账套列表
   */
  getSetOfBookList = () => {
    const { searchForm } = this.state;
    baseService.getSetOfBooksByTenant().then(res => {
      const list = [];
      // eslint-disable-next-line array-callback-return
      res.data.map(item => {
        list.push({ value: item.id, label: `${item.setOfBooksCode}-${item.setOfBooksName}` });
      });
      const form = searchForm;

      form[0].options = list;

      this.setState({ searchForm: form });
    });
  };

  /**
   * 分配公司
   */
  handleDistribute = record => {
    const { dispatch } = this.props;
    const { companyDistribution } = this.state;
    dispatch(
      routerRedux.push({
        pathname: companyDistribution
          .replace(':setOfBooksId', record.setOfBooksId)
          .replace(':id', record.id),
      })
    );
  };

  /**
   * 编辑修改
   */
  editItem = (e, record) => {
    console.log(record);
    const { slideParams } = this.state;
    slideParams.record = record;
    this.setState({ slideParams }, () => {
      this.showSlide(true);
    });
  };

  /**
   * 模糊搜索查询
   */
  search = result => {
    const { company } = this.props;
    const { searchParams } = this.state;
    this.setState(
      {
        searchParams: {
          setOfBooksId: result.setOfBooksId ? result.setOfBooksId : company.setOfBooksId,
          acpReqTypeCode: result.acpReqTypeCode ? result.acpReqTypeCode : '',
          description: result.description ? result.description : '',
        },
      },
      () => {
        // eslint-disable-next-line react/no-string-refs
        this.refs.table.search(searchParams);
      }
    );
  };

  searchEventHandle = (event, value) => {
    const { searchParams } = this.state;
    if (event === 'SETOFBOOKID') {
      // eslint-disable-next-line no-param-reassign
      value = value || '';
      this.setState(
        { searchParams: { setOfBooksId: value }, slideParams: { setOfBooksId: value } },
        () => {
          if (value) {
            // eslint-disable-next-line react/no-string-refs
            this.refs.table.search(searchParams);
          }
        }
      );
    }
  };

  /**
   * 清空
   */
  clear = () => {
    const { company } = this.props;
    const { slideParams, searchParams } = this.state;
    this.setState(
      {
        searchParams: {
          setOfBooksId: company.setOfBooksId,
          acpReqTypeCode: '',
          description: '',
        },
        slideParams: { ...slideParams, setOfBooksId: company.setOfBooksId },
      },
      () => {
        // eslint-disable-next-line react/no-string-refs
        this.refs.table.search(searchParams);
      }
    );
  };

  /**
   * 侧边栏的显示
   */
  showSlide = flag => {
    const { searchParams } = this.state;
    this.setState({ showSlideFrame: flag }, () => {
      if (flag) {
        // eslint-disable-next-line react/no-string-refs
        this.refs.table.search(searchParams);
      }
      // !flag && this.refs.table.search(searchParams);
    });
  };

  /**
   * 新建
   */
  handleNew = () => {
    const { searchParams } = this.state;
    const { company } = this.props;
    this.setState(
      {
        slideParams: {
          setOfBooksId: searchParams.setOfBooksId
            ? searchParams.setOfBooksId
            : company.setOfBooksId,
        },
      },
      () => {
        this.showSlide(true);
      }
    );
  };

  /**
   * 关闭侧边栏
   */
  afterClose = flag => {
    const { searchParams } = this.state;
    this.setState(
      {
        showSlideFrame: false,
      },
      () => {
        if (flag) {
          // eslint-disable-next-line react/no-string-refs
          this.refs.table.search(searchParams);
        }
        // flag && this.refs.table.search(searchParams);
      }
    );
  };

  render() {
    const { columns, searchForm, showSlideFrame, slideParams, searchParams } = this.state;
    return (
      <div className="budget-organization">
        {/* <h3 className="header-title">付款申请单类型定义</h3> */}
        <SearchArea
          searchForm={searchForm}
          submitHandle={this.search}
          clearHandle={this.clear}
          eventHandle={this.searchEventHandle}
          maxLength={4}
        />
        <div className="divider" />
        <div className="table-header">
          {/* <div className="table-header-title">{`共搜索到 ${pagination.total} 条数据`}</div> */}
          <Row>
            <Col span={18}>
              <div className="table-header-buttons">
                <Button type="primary" onClick={this.handleNew}>
                  {this.$t({ id: 'common.create' })}
                </Button>{' '}
                {/* 新建 */}
              </div>
            </Col>
            {/* <Col span={6}>
              <Search placeholder="请输入账套" onSearch={this.onDocumentSearch} enterButton />
            </Col> */}
          </Row>
        </div>
        <CustomTable
          columns={columns}
          // eslint-disable-next-line react/no-string-refs
          ref="table"
          url={`${config.payUrl}/api/acp/request/type/query`}
          params={searchParams}
        />
        <SlideFrame
          title={slideParams.record ? '编辑单据类型' : '新建单据类型'}
          show={showSlideFrame}
          afterClose={this.afterClose}
          onClose={() => this.showSlide(false)}
        >
          <FundDocumentsAdd
            onClose={e => this.showSlide(e)}
            params={{ editFlag: showSlideFrame, record: slideParams }}
          />
        </SlideFrame>
      </div>
    );
  }
}

function mapStateToProps(state) {
  return {
    company: state.user.company,
  };
}

export default connect(
  mapStateToProps,
  null,
  null,
  { withRef: true }
)(FundDocumentsType);
