import React from 'react';
import { connect } from 'dva';
import config from 'config';
import { Badge, Button, Popover, message, Row, Col, Input } from 'antd';
import { routerRedux } from 'dva/router';
import httpFetch from 'share/httpFetch';
import AcpRequestTypeDetail from './new-acp-request-type';
import SlideFrame from 'widget/slide-frame';
import SearchArea from 'widget/search-area';
import baseService from 'share/base.service';
import CustomTable from 'widget/custom-table';
const Search = Input.Search;

class AcpRequestType extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      loading: true,
      data: [],
      page: 0,
      pageSize: 10,
      columns: [
        {
          title: this.$t('payment.zhang.set') /*账套*/,
          dataIndex: 'setOfBooksName',
          width: 196,
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
          title: this.$t('payment.payment.requisition.type.code') /*付款申请单类型代码*/,
          dataIndex: 'acpReqTypeCode',
          width: 196,
          render: recode => (
            <span>
              <Popover content={recode}>{recode ? recode : '-'}</Popover>
            </span>
          ),
        },
        {
          title: this.$t('payment.payment.requisition.type.name') /*付款申请单类型名称*/,
          dataIndex: 'description',
          width: 196,
          render: description => (
            <Popover content={description}>{description ? description : '-'}</Popover>
          ),
        },

        {
          title: this.$t('payment.associated.form.type') /*关联表单类型*/,
          dataIndex: 'formName',
          width: 196,
          render: recode => (
            <span>
              <Popover content={recode}>{recode ? recode : '-'}</Popover>
            </span>
          ),
        },
        {
          title: this.$t('paymentmethod.isenabled') /*状态*/,
          dataIndex: 'enabled',
          width: 100,
          align: 'center',
          render: isEnabled => (
            <Badge
              status={isEnabled ? 'success' : 'error'}
              text={isEnabled ? this.$t('payment.to.enable.the') : this.$t('payment.disable')}
            />
          ),
        },
        {
          title: this.$t('payment.operation') /*操作*/,
          key: 'operation',
          width: 146,
          align: 'center',
          render: (text, record) => (
            <span>
              <a onClick={e => this.editItem(e, record)}>{this.$t('payment.the.editor')}</a>
              {/*编辑*/}
              <span className="ant-divider" />
              <a onClick={() => this.handleDistribute(record)}>
                {this.$t('payment.distribution.of.the.company')}
              </a>
              {/*公司分配*/}
            </span>
          ),
        }, //操作
      ],
      pagination: { total: 0 },
      searchForm: [
        {
          type: 'select',
          id: 'setOfBooksId',
          label: this.$t('payment.zhang.set') /*账套*/,
          options: [],
          labelKey: 'name',
          valueKey: 'id',
          isRequired: true,
          event: 'SETOFBOOKID',
          defaultValue: this.props.company.setOfBooksId,
          colSpan: '6',
        }, //账套
        {
          type: 'input',
          id: 'acpReqTypeCode',
          label: this.$t('payment.payment.requisition.type.code'),
          colSpan: '6',
        } /*付款申请单类型代码*/,
        {
          type: 'input',
          id: 'description',
          label: this.$t('payment.payment.requisition.type.name'),
          colSpan: '6',
        } /*付款申请单类型名称*/,
      ],
      searchParams: {
        setOfBooksId: this.props.company.setOfBooksId,
        acpReqTypeCode: '',
        description: '',
      },
      showSlideFrame: false,
      slideParams: {},
      companyDistribution:
        '/document-type-manage/payment-requisition-type/distribution-company/:setOfBooksId/:id', //公司分配
    };
  }

  editItem = (e, record) => {
    let slideParams = this.state.slideParams;
    slideParams.record = record;
    this.setState({ slideParams }, () => {
      this.showSlide(true);
    });
  };

  //分配公司
  handleDistribute = record => {
    this.props.dispatch(
      routerRedux.push({
        pathname: this.state.companyDistribution
          .replace(':setOfBooksId', record.setOfBooksId)
          .replace(':id', record.id),
      })
    );
  };

  componentWillMount() {
    this.getSetOfBookList();
  }

  //获取账套列表
  getSetOfBookList = () => {
    baseService.getSetOfBooksByTenant().then(res => {
      let list = [];
      res.data.map(item => {
        list.push({ value: item.id, label: `${item.setOfBooksCode}-${item.setOfBooksName}` });
      });
      let form = this.state.searchForm;

      form[0].options = list;

      this.setState({ searchForm: form });
    });
  };

  search = result => {
    this.setState(
      {
        searchParams: {
          setOfBooksId: result.setOfBooksId ? result.setOfBooksId : this.props.company.setOfBooksId,
          acpReqTypeCode: result.acpReqTypeCode ? result.acpReqTypeCode : '',
          description: result.description ? result.description : '',
        },
      },
      () => {
        this.refs.table.search(this.state.searchParams);
      }
    );
  };

  clear = () => {
    this.setState(
      {
        searchParams: {
          setOfBooksId: this.props.company.setOfBooksId,
          acpReqTypeCode: '',
          description: '',
        },
        slideParams: { ...this.state.slideParams, setOfBooksId: this.props.company.setOfBooksId },
      },
      () => {
        this.refs.table.search(this.state.searchParams);
      }
    );
  };

  showSlide = flag => {
    this.setState({ showSlideFrame: flag }, () => {
      !flag && this.refs.table.search(this.state.searchParams);
    });
  };

  searchEventHandle = (event, value) => {
    if (event == 'SETOFBOOKID') {
      value = value ? value : '';
      this.setState(
        { searchParams: { setOfBooksId: value }, slideParams: { setOfBooksId: value } },
        () => {
          if (value) {
            this.refs.table.search(this.state.searchParams);
          }
        }
      );
    }
  };

  handleNew = () => {
    let searchParams = this.state.searchParams;
    this.setState(
      {
        slideParams: {
          setOfBooksId: searchParams.setOfBooksId
            ? searchParams.setOfBooksId
            : this.props.company.setOfBooksId,
        },
      },
      () => {
        this.showSlide(true);
      }
    );
  };

  afterClose = flag => {
    this.setState(
      {
        showSlideFrame: false,
      },
      () => {
        flag && this.refs.table.search(this.state.searchParams);
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
                <Search
                 placeholder={this.$t("payment.please.enter.the.account")}
                 onSearch={this.onDocumentSearch}
                 enterButton
                />
            </Col> */}
          </Row>
        </div>
        <CustomTable
          columns={columns}
          ref="table"
          url={`${config.payUrl}/api/acp/request/type/query`}
          params={searchParams}
        />
        <SlideFrame
          title={
            slideParams.record
              ? this.$t('payment.edit.this.payment.request.type')
              : this.$t('payment.the.new.payment.application.type')
          } /*新建付款申请单类型*/ /*编辑付款申请单类型*/
          show={showSlideFrame}
          afterClose={this.afterClose}
          onClose={() => this.showSlide(false)}
        >
          <AcpRequestTypeDetail
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
)(AcpRequestType);
