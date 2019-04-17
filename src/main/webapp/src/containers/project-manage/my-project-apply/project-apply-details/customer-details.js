import React, { Component } from 'react';
import BasicInfo from 'components/Widget/basic-info';
import { Affix, Button } from 'antd';

class CustomerDetails extends Component {
  constructor(props) {
    super(props);
    this.state = {
      infoList: [
        {
          type: 'input',
          id: 'customerCode',
          label: '客户编号',
        },
        {
          type: 'input',
          id: 'customerName',
          isRequired: true,
          label: '客户名称',
        },
        {
          type: 'input',
          id: 'customerType',
          isRequired: true,
          label: '客户类型',
        },
        {
          type: 'input',
          id: 'people',
          label: '联系人',
        },
        {
          type: 'input',
          id: 'address',
          label: '地址',
        },
        {
          type: 'input',
          id: 'phone',
          label: '联系电话',
        },
        {
          type: 'input',
          id: 'taxPlayerName',
          label: '纳税人名称',
        },
        {
          type: 'input',
          id: 'taxPlayerIdeNumber',
          label: '纳税人识别号',
        },
        {
          type: 'input',
          id: 'taxPlayerPhone',
          label: '电话',
        },
        {
          type: 'input',
          id: 'bank',
          label: '银行开户行',
        },
        {
          type: 'input',
          id: 'bankNumber',
          label: '银行账号',
        },
        {
          type: 'input',
          id: 'email',
          label: '邮箱',
        },
        {
          type: 'input',
          id: 'phoneNumber',
          label: '手机',
        },
        {
          type: 'input',
          id: 'emailAddress',
          label: '邮箱地址',
        },
        {
          type: 'input',
          id: 'recipient',
          label: '收件人',
        },
        {
          type: 'input',
          id: 'recipientPhone',
          label: '收件人电话',
        },
        {
          type: 'input',
          id: 'legalPerson',
          label: '法人',
        },
        {
          type: 'input',
          id: 'company',
          label: '机构',
        },
      ],
      infoDate: {},
    };
  }

  render() {
    const { infoDate, infoList } = this.state;
    const { readOnly } = this.props;
    return (
      <div>
        <BasicInfo isHideEditBtn infoList={infoList} infoData={infoDate} />
        {readOnly ? (
          <div />
        ) : (
          <Affix
            offsetBottom={0}
            className="bottom-bar bottom-bar-approve"
            style={{
              position: 'fixed',
              bottom: 0,
              width: '100%',
              height: '50px',
              boxShadow: '0px -5px 5px rgba(0, 0, 0, 0.15)',
              background: '#fff',
              lineHeight: '50px',
              zIndex: 1,
            }}
          >
            <Button onClick={this.handleReturn} />
          </Affix>
        )}
      </div>
    );
  }
}

export default CustomerDetails;
