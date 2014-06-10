package org.cad.interruptus.core.esper;

import com.espertech.esper.client.EventBean;
import com.espertech.esper.client.UpdateListener;
import com.espertech.esper.client.EPAdministrator;
import com.espertech.esper.client.EPStatement;
import com.espertech.esper.client.EPStatementException;
import com.espertech.esper.client.EPStatementState;
import com.google.common.collect.Lists;
import java.util.List;
import org.cad.interruptus.entity.Statement;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.logging.Log;

public class StatementConfiguration implements EsperConfiguration<Statement>
{
    final Log logger = LogFactory.getLog(getClass());
    final EPAdministrator epAdministrator;

    public StatementConfiguration(final EPAdministrator epAdministrator)
    {
        this.epAdministrator = epAdministrator;
    }

    @Override
    public List<String> list()
    {
        final String[] statementNames = epAdministrator.getStatementNames();
        final List<String> statements = Lists.newArrayList(statementNames);

        return statements;
    }

    @Override
    public void save(final Statement statement) throws EPStatementException
    {
        final String name          = statement.getName();
        final String query         = statement.getQuery();
        final EPStatement existing = epAdministrator.getStatement(name);

        if (existing != null) {
            logger.info("Existing statement detected for : " + name);
            remove(name);
        }

        logger.info("Saving statement : " + name);

        final EPStatement sttm = epAdministrator.createEPL(query, name);

        if ( ! statement.isRunning() && ! sttm.isStopped()) {
            sttm.stop();
        }

        if ( ! statement.getDebug()) {
            return;
        }

        sttm.addListener(new UpdateListener() {
            @Override
            public void update(EventBean[] newEvents, EventBean[] oldEvents)
            {
                for (EventBean newEvent : newEvents) {
                    logger.info(newEvent.getUnderlying());
                }
            }
        });
    }

    @Override
    public Boolean start(final String name)
    {
        try {
            final EPStatement sttm = epAdministrator.getStatement(name);

            if (sttm == null) {
                return false;
            }

            if (sttm.isStarted()) {
                return true;
            }

            logger.info("Starting statement : " + name);
            sttm.start();

            return true;
        } catch (EPStatementException e) {
            logger.info(e.getMessage());
            return false;
        }
    }

    @Override
    public Boolean stop(final String name)
    {
        try {
            final EPStatement epStatement = epAdministrator.getStatement(name);

            if (epStatement == null) {
                return false;
            }

            if (epStatement.isStopped()) {
                return true;
            }

            logger.info("Stoping statement : " + name);
            epStatement.stop();

            return true;
        } catch (EPStatementException e) {
            logger.info(e.getMessage());
            return false;
        }
    }

    public EPStatementState getStatementState(final String statementName) throws EPStatementException
    {
        try {
            final EPStatement epStatement = epAdministrator.getStatement(statementName);

            if (epStatement != null) {
                return epStatement.getState();
            }

        } catch (EPStatementException e) {
            logger.info(e.getMessage());
        }

        return null;
    }

    @Override
    public Boolean remove(final String name)
    {
        try {
            final EPStatement epStatement = epAdministrator.getStatement(name);

            if (epStatement != null) {
                logger.info("Removing statement : " + name);
                epStatement.destroy();
            }

            return true;

        } catch (EPStatementException e) {
            logger.info(e.getMessage());
            return false;
        }
    }
}
