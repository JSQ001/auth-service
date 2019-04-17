import React from 'react';

import { Form, Upload, Icon, message } from 'antd';
const Dragger = Upload.Dragger;
import PropTypes from 'prop-types';
import config from 'config';
import { connect } from 'dva';

/**
 * 上传附件组件
 * @params extensionName: 附件支持的扩展名
 * @params fileNum: 最大上传文件的数量
 * @params attachmentType: 附件类型
 * @params uploadHandle: 获取上传文件的Oid
 */

class UploadFile extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      fileList: props.defaultFileList || [],
      Oids: props.defaultOids || [],
      defaultListTag: true,
    };
    console.log(props.defaultOids);
  }

  handleData = () => {
    return {
      attachmentType: this.props.attachmentType,
    };
  };

  beforeUpload = file => {
    const isLt3M = file.size / 1024 / 1024 <= 3;
    if (!isLt3M) {
      message.error(this.$t('upload.isLt3M'));
    }
    return isLt3M;
  };

  handleChange = info => {
    if (info.file.size / 1024 / 1024 > 3) return;

    this.setState({ defaultListTag: false });
    const fileNum = parseInt(`-${this.props.fileNum}`);
    let fileList = info.fileList;
    let Oids = this.state.Oids;
    fileList = fileList.slice(fileNum);
    this.setState({ fileList }, () => {
      const status = info.file.status;
      if (status === 'done') {
        message.success(`${info.file.name} ${this.$t('upload.success') /*上传成功*/}`);
        Oids.push(info.file.response.attachmentOid);
        Oids = Oids.slice(fileNum);
        this.setState({ Oids }, () => {
          this.props.uploadHandle(this.props.needAllResponse ? fileList : Oids);
        });
      } else if (status === 'error') {
        message.error(`${info.file.name} ${this.$t('upload.fail') /*上传失败*/}`);
      }
    });
  };

  handleRemove = info => {
    this.setState({ defaultListTag: false });
    let Oids = this.state.Oids;
    let fileList = this.state.fileList;
    Oids.map(Oid => {
      Oid === (info.response ? info.response.attachmentOid : info.attachmentOid) &&
        Oids.delete(Oid);
    });
    fileList.map(item => {
      (item.response ? item.response.attachmentOid : item.attachmentOid) ===
        (info.response ? info.response.attachmentOid : info.attachmentOid) && fileList.delete(item);
    });
    this.setState({ Oids }, () => {
      this.props.uploadHandle(this.props.needAllResponse ? this.state.fileList : this.state.Oids);
    });
  };

  render() {
    const upload_headers = {
      Authorization: 'Bearer ' + window.sessionStorage.getItem('token'),
      Accept: 'application/json, text/plain, */*',
    };
    const { fileList } = this.state;
    const { defaultFileList, disabled } = this.props;
    return (
      <div className="upload">
        <Dragger
          name="file"
          action={this.props.uploadUrl}
          headers={upload_headers}
          data={this.handleData}
          beforeUpload={this.beforeUpload}
          onChange={this.handleChange}
          onRemove={this.handleRemove}
          disabled={disabled}
          style={{ padding: '20px 0' }}
          defaultFileList={defaultFileList}
          multiple
          fileList={fileList}
        >
          <p className="ant-upload-drag-icon">
            <Icon type="inbox" />
          </p>
          <p className="ant-upload-text">{this.$t('upload.info') /*点击或将文件拖拽到这里上传*/}</p>
          <p className="ant-upload-hint">
            {this.$t('upload.support.extension') /*支持扩展名*/}：{this.props.extensionName}
          </p>
        </Dragger>
      </div>
    );
  }
}

UploadFile.propTypes = {
  uploadUrl: PropTypes.string, //上传URL
  attachmentType: PropTypes.string.isRequired, //附件类型
  extensionName: PropTypes.string, //附件支持的扩展名
  fileNum: PropTypes.number, //最大上传文件的数量
  defaultFileList: PropTypes.array, //默认上传的文件列表，每项必须包含：uid，name
  defaultOids: PropTypes.array, //默认上传的文件列表Oid
  needAllResponse: PropTypes.bool, //是否返回上传文件的所有内容，为false时只返回Oid
  uploadHandle: PropTypes.func, //获取上传文件的Oid
};

UploadFile.defaultProps = {
  uploadUrl: `${config.baseUrl}/api/upload/attachment`,
  extensionName: '.rar .zip .doc .docx .pdf .jpg...',
  fileNum: 0,
  defaultFileList: [],
  defaultOids: [],
  needAllResponse: false,
  uploadHandle: () => {},
};

function mapStateToProps(state) {
  return {
    //authToken: window.sessionStorage.getItem('token')
  };
}

const WrappedUploadFile = Form.create()(UploadFile);

export default connect(
  mapStateToProps,
  null,
  null,
  { withRef: true }
)(WrappedUploadFile);