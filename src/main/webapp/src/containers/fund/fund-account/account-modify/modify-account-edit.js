import React from 'react';
import { connect } from 'dva';
import { Form } from 'antd';
import ModifyAccountEditInformation from './modify-account-edit-information';

class ModifyAccountEdit extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      //   editApplicationInformation: {},
    };
  }

  componentWillMount() {
    const { params } = this.props;
    console.log('===========22===========');
    console.log(params);
    console.log('===========22===========');
    this.setState({ editApplicationInformation: params });
    // console.log(editApplicationInformation);

    // editApplicationInformation =  this.props.editModel ;
    // this.getList();
    // console.log();
  }

  render() {
    const { editApplicationInformation } = this.state;
    return (
      <div>
        <ModifyAccountEditInformation editApplicationInformation={editApplicationInformation} />
      </div>
    );
  }
}

function map(state) {
  return {
    user: state.user.currentUser,
    company: state.user.company,
  };
}

export default connect(map)(Form.create()(ModifyAccountEdit));
