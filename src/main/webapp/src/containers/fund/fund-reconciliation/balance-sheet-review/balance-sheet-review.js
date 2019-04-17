import React from 'react';
import { connect } from 'dva';
import { Table, Form, Row, Col, Button } from 'antd';
import FundSearchForm from '../../fund-components/fund-search-form';

class BalanceSheetReview extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
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
          id: 'billType',
          options: [],
          valueListCode: 'ZJ_FORM_TYPE',
        },
        {
          colSpan: 6,
          type: 'valueList',
          label: '期间',
          id: 'during',
          options: [],
          valueListCode: 'ZJ_FORM_TYPE',
        },
      ],
      columns: [
        {
          title: '银行存款余额调节表',
          children: [
            {
              title: '单位名称：',
              width: '45%',
              key: '',
              value: '123',
              children: [
                {
                  title: '项目及经济业务内容',
                  width: 170,
                  children: [
                    {
                      title: 'ORACLE银行日记账余额',
                      width: 170,
                      children: [
                        {
                          title: '加：企业未收银行已收',
                          width: 170,
                          children: [
                            {
                              title: '加项小计',
                              width: 170,
                              children: [
                                {
                                  title: '加：企业未收银行已收',
                                  width: 170,
                                  children: [
                                    {
                                      title: '减项小计',
                                      width: 170,
                                    },
                                  ],
                                },
                              ],
                            },
                          ],
                        },
                      ],
                    },
                  ],
                },
                {
                  title: '摘要',
                  width: 100,
                  children: [
                    {
                      title: '',
                      width: 100,
                      children: [
                        {
                          title: '',
                          width: 100,
                          children: [
                            {
                              title: '',
                              width: 100,
                              children: [
                                {
                                  title: '',
                                  width: 100,
                                  children: [
                                    {
                                      title: '',
                                      width: 100,
                                    },
                                  ],
                                },
                              ],
                            },
                          ],
                        },
                      ],
                    },
                  ],
                },
                {
                  title: '业务发生时间',
                  width: 100,
                  children: [
                    {
                      title: '',
                      width: 100,
                      children: [
                        {
                          title: '',
                          width: 100,
                          children: [
                            {
                              title: '',
                              width: 100,
                              children: [
                                {
                                  title: '',
                                  width: 100,
                                  children: [
                                    {
                                      title: '',
                                      width: 100,
                                    },
                                  ],
                                },
                              ],
                            },
                          ],
                        },
                      ],
                    },
                  ],
                },
                {
                  title: '金额',
                  width: 100,
                  children: [
                    {
                      title: '',
                      width: 100,
                      children: [
                        {
                          title: '',
                          width: 100,
                          children: [
                            {
                              title: '',
                              width: 100,
                              children: [
                                {
                                  title: '',
                                  width: 100,
                                  children: [
                                    {
                                      title: '',
                                      width: 100,
                                    },
                                  ],
                                },
                              ],
                            },
                          ],
                        },
                      ],
                    },
                  ],
                },
              ],
            },
            {
              title: '银行账号：',
              width: '45%',
              children: [
                {
                  title: '项目及经济业务内容',
                  width: 170,
                  children: [
                    {
                      title: 'ORACLE银行日对账余额',
                      width: 170,
                      children: [
                        {
                          title: '加：企业未收企业已收',
                          width: 170,
                          children: [
                            {
                              title: '加项小计',
                              width: 170,
                              children: [
                                {
                                  title: '加：企业未收企业已收',
                                  width: 170,
                                  children: [
                                    {
                                      title: '减项小计',
                                      width: 170,
                                    },
                                  ],
                                },
                              ],
                            },
                          ],
                        },
                      ],
                    },
                  ],
                },
                {
                  title: '凭证号',
                  width: 100,
                  children: [
                    {
                      title: '',
                      width: 100,
                      children: [
                        {
                          title: '',
                          width: 100,
                          children: [
                            {
                              title: '',
                              width: 100,
                              children: [
                                {
                                  title: '',
                                  width: 100,
                                  children: [
                                    {
                                      title: '',
                                      width: 100,
                                    },
                                  ],
                                },
                              ],
                            },
                          ],
                        },
                      ],
                    },
                  ],
                },
                {
                  title: '业务入账时间',
                  width: 100,
                  children: [
                    {
                      title: '',
                      width: 100,
                      children: [
                        {
                          title: '',
                          width: 100,
                          children: [
                            {
                              title: '',
                              width: 100,
                              children: [
                                {
                                  title: '',
                                  width: 100,
                                  children: [
                                    {
                                      title: '',
                                      width: 100,
                                    },
                                  ],
                                },
                              ],
                            },
                          ],
                        },
                      ],
                    },
                  ],
                },
                {
                  title: '金额',
                  width: 100,
                  children: [
                    {
                      title: '',
                      width: 100,
                      children: [
                        {
                          title: '',
                          width: 100,
                          children: [
                            {
                              title: '',
                              width: 100,
                              children: [
                                {
                                  title: '',
                                  width: 100,
                                  children: [
                                    {
                                      title: '',
                                      width: 100,
                                    },
                                  ],
                                },
                              ],
                            },
                          ],
                        },
                      ],
                    },
                  ],
                },
              ],
            },
          ],
        },
      ],
    };
  }

  componentDidMount() {}

  render() {
    const { columns, searchForm } = this.state;
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
          <Table
            columns={columns}
            // dataSource={data}
            bordered
            size="middle"
            // scroll={{ x: '139%', y: 240}}
          />
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
