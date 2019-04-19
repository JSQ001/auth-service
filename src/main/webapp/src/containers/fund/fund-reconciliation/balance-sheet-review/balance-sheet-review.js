import React from 'react';
import { connect } from 'dva';
import { Form, Row, Col, Button } from 'antd';
import FundSearchForm from '../../fund-components/fund-search-form';
import balanceSheetServicen from './balance-sheet-review.service';
/* eslint-disable */
let periodData = [];
class BalanceSheetReview extends React.Component {
  constructor(props) {
    super(props);

    this.state = {
      tableData: [], // 余额调节表detail
      // loading: false,
      searchForm: [
        {
          colSpan: 6,
          type: 'input',
          label: '公司',
          id: 'company',
        },
        {
          colSpan: 6,
          type: 'modalList',
          label: '银行账号',
          id: 'bankNumber',
          listType: 'paymentAccount',
        },
        {
          colSpan: 6,
          type: 'valueList',
          label: '币种',
          id: 'currency',
          options: [],
          valueListCode: 'ZJ_FORM_TYPE',
        },
        {
          colSpan: 6,
          type: 'valueList',
          label: '期间',
          id: 'periodName',
          options: [],
          customizeOptions: periodData,
        },
      ],
    };
  }

  componentWillMount() {
    this.getperiodData();
  }

  componentDidMount() {
    // this.getList();
  }

  // 获取期间列表
  getperiodData = () => {
    balanceSheetServicen.getPeriod().then(response => {
      const { data } = response;
      console.log(data);
      const period = [];
      // let index = 0;
      periodData = data.map(Item => {
        period.value = Item.periodNum;
        period.name = Item.periodName;
        return Item;
      });
      console.log(periodData);
    });
  };

  handleSearch = values => {
    console.log(values);
    const { searchParams } = this.state;
    this.setState(
      {
        searchParams: {
          ...searchParams,
          accountId: values.bankNumber ? values.bankNumber.id : '', // bank id
          currency: values.currency ? values.currency : '', // 币种
          periodName: values.periodName ? values.periodName : '', // 期间
        },
      },
      () => {
        this.getList();
      }
    );
  };

  // 获取列表
  getList = () => {
    const { searchParams } = this.state;
    // this.setState({ loading: true });
    balanceSheetServicen.getAccountCheckList(searchParams).then(response => {
      const { data } = response;
      this.setState({
        tableData: data,
        // loading: false,
      });
    });
  };

  render() {
    const { searchForm, tableData } = this.state;
    return (
      <div className="balance-sheet">
        <div className="common-top-area">
          <Row>
            <FundSearchForm
              submitHandle={this.handleSearch}
              searchForm={searchForm}
              // maxLength={4}
            />
          </Row>
        </div>
        <div>
          <div style={{ marginBottom: '10px' }}>
            <Row>
              <Col span={10}>
                <Button style={{ marginRight: '8px' }} type="primary">
                  复核
                </Button>
                <Button
                  style={{ marginRight: '8px' }}
                  // disabled={selectedRow.length === 0}
                  type="primary"
                >
                  取消复核
                </Button>
                <Button type="primary">打印</Button>
              </Col>
            </Row>
          </div>
          {/* <Table
            columns={columns}
            // dataSource={data}
            bordered
            size="middle"
            // scroll={{ x: '139%', y: 240}}
          /> */}
          <table
            border="1"
            style={{ tableLayout: 'fixed', border: '1px solid #ccc' }}
            width="98%"
            height="450px"
          >
            <tr>
              <th colSpan="8" style={{ textAlign: 'center' }}>
                银行存款余额调节表
              </th>
            </tr>
            <tr>
              <td style={{ paddingLeft: '150px' }} colSpan="4">
                单位名称:
              </td>
              <td style={{ paddingLeft: '150px' }} colSpan="4">
                银行账号:
              </td>
            </tr>
            <tr style={{ textAlign: 'center' }}>
              <th style={{ width: '240px' }}>项目及经济业务内容</th>
              <th>摘要</th>
              <th>业务发生时间</th>
              <th>金额</th>
              <th>项目及经济业务内容</th>
              <th>凭证号</th>
              <th>业务入账时间</th>
              <th>金额</th>
            </tr>
            <tr>
              <th style={{ width: '240px' }} align="left">
                ORACLE银行日记账余额
              </th>
              <td>1</td>
              <td>1</td>
              <td>1</td>
              <th align="left">ORACLE银行对账单余额</th>
              <td>a</td>
              <td>a</td>
              <td>a</td>
            </tr>
            <tr>
              <td width="240px" align="right">
                加：企业未收银行已收
              </td>
              <td>1</td>
              <td>1</td>
              <td>1</td>
              <td align="right">加：企业未收企业已收</td>
              <td>a</td>
              <td>a</td>
              <td>a</td>
            </tr>
            <tr>
              <td width="240px" align="right">
                加项小计
              </td>
              <td>1</td>
              <td>1</td>
              <td>1</td>
              <td align="right">加项小计</td>
              <td>a</td>
              <td>a</td>
              <td>a</td>
            </tr>
            <tr>
              <td width="240px">加：企业未收银行已收</td>
              <td>1</td>
              <td>1</td>
              <td>1</td>
              <td>加：企业未收银行已收</td>
              <td>a</td>
              <td>a</td>
              <td>a</td>
            </tr>
            <tr>
              <td width="240px" align="right">
                减项小计
              </td>
              <td>1</td>
              <td>1</td>
              <td>1</td>
              <td align="right">减项小计</td>
              <td>a</td>
              <td>a</td>
              <td>a</td>
            </tr>
            <tr>
              <td style={{ paddingRight: '80px' }} colSpan="4" align="right">
                调整后金额
              </td>
              <td colSpan="4" align="right" style={{ paddingRight: '80px' }}>
                调整后金额
              </td>
            </tr>
            <tr>
              <td style={{ paddingLeft: '10px' }} colSpan="4">
                制表人：
              </td>
              <td style={{ paddingLeft: '10px' }} colSpan="4">
                复核人：
              </td>
            </tr>
          </table>
        </div>
      </div>
    );
  }
}

const wrappedCompanyDistribution = Form.create()(BalanceSheetReview);

export default connect()(wrappedCompanyDistribution);
