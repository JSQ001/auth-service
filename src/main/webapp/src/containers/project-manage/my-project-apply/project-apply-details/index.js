import React, { Component } from 'react';
import ProjectDetailsCommon from './project-details-common';

class ProjectDetailsIndex extends Component {
  constructor(props) {
    super(props);
    this.state = {};
  }

  render() {
    const { match } = this.props;
    return (
      <div>
        <ProjectDetailsCommon match={match} />
      </div>
    );
  }
}

export default ProjectDetailsIndex;
